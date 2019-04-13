package org.furcoder.zero_caterpillar.jsonpath;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.furcoder.zero_caterpillar.util.BiConsumer;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;

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
		var json = parseContext.parse(jsonStr);
		var path = MessageFormat.format(pathSrc, args);

		Class<?> clazz = object.getClass();
		BiConsumer<AccessibleObject[], BiConsumer<AccessibleObject, Object>> processProperties = (elems, setter) -> {
			for (var elem : elems)
			{
				var anno = elem.getAnnotation(JsonPathProperty.class);
				if (anno == null) continue;

				var propertyPath = path + "." + MessageFormat.format(anno.value(), args);
				Object value = json.read(propertyPath, clazz);
				elem.setAccessible(true);
				setter.accept(elem, value);
			}
		};

		processProperties.accept(clazz.getDeclaredFields(), (field, value) -> ((Field) field).set(object, value));
		processProperties.accept(clazz.getDeclaredMethods(), (method, value) -> ((Method) method).invoke(object, value));
		return object;
	}
}
