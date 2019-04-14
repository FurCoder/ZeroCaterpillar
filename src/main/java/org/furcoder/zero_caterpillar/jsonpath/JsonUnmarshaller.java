package org.furcoder.zero_caterpillar.jsonpath;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.furcoder.zero_caterpillar.util.ThrowingBiConsumer;
import org.furcoder.zero_caterpillar.util.ThrowingTriConsumer;

import java.lang.reflect.*;
import java.text.MessageFormat;
import java.util.function.Function;

@UtilityClass
public class JsonUnmarshaller
{
	static final ObjectMapper objectMapper = new ObjectMapper();
	static final ParseContext parseContext;

	static
	{
		objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		objectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);

		Configuration conf = Configuration.builder().jsonProvider(new JacksonJsonProvider(objectMapper)).build();
		parseContext = JsonPath.using(conf);
	}


	@SneakyThrows
	public <T> T unmarshal(String jsonStr, String path, Class<T> clazz, Object... args)
	{
		var constructor = (Constructor<T>) clazz.getDeclaredConstructors()[0];
		constructor.setAccessible(true);
		return unmarshal(jsonStr, path, constructor.newInstance(), args);
	}

	@SneakyThrows
	public <T> T unmarshal(String jsonStr, String pathSrc, T object, Object... args)
	{
		Function<String, String> formatPath = (str) -> (args.length == 0) ? str : MessageFormat.format(str, args);

		var json = parseContext.parse(jsonStr);
		var path = formatPath.apply(pathSrc);

		Class<?> clazz = object.getClass();
		ThrowingTriConsumer<AccessibleObject[], Function<AccessibleObject, Type>, ThrowingBiConsumer<AccessibleObject, Object>> processProperties = (elems, getType, setter) -> {
			for (var elem : elems)
			{
				var anno = elem.getAnnotation(JsonPathProperty.class);
				if (anno == null) continue;

				var subPath = !anno.value().isEmpty() ? formatPath.apply(anno.value()) : ((Member) elem).getName();
				var propertyPath = path + "." + subPath;
				Object value = null;

				var propertyType = getType.apply(elem);
				if (propertyType instanceof ParameterizedType)
				{
					var parameterizedType = (ParameterizedType) propertyType;
					var actualTypeArguments = parameterizedType.getActualTypeArguments();
					var rawClass = (Class<?>) parameterizedType.getRawType();

					var typeFactory = objectMapper.getTypeFactory();
					var javaType = (actualTypeArguments.length == 1) ?
							typeFactory.constructParametricType(rawClass, (Class<?>) actualTypeArguments[0]) :
							typeFactory.constructMapLikeType(rawClass, (Class<?>) actualTypeArguments[0], (Class<?>) actualTypeArguments[1]);

					value = json.read(propertyPath, rawClass);
					value = objectMapper.convertValue(value, javaType);
				}
				else
				{
					value = json.read(propertyPath, (Class<?>) propertyType);
				}

				elem.setAccessible(true);
				setter.accept(elem, value);
			}
		};

		processProperties.accept(clazz.getDeclaredFields(), (f) -> ((Field) f).getType(), (f, value) -> {
			((Field) f).set(object, value);
		});
		processProperties.accept(clazz.getDeclaredMethods(), (m) -> ((Method) m).getGenericParameterTypes()[0], (m, value) -> {
			((Method) m).invoke(object, value);
		});
		return object;
	}
}
