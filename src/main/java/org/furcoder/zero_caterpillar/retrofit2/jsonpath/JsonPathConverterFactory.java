package org.furcoder.zero_caterpillar.retrofit2.jsonpath;

import okhttp3.ResponseBody;
import org.furcoder.zero_caterpillar.jsonpath.JsonUnmarshaller;
import retrofit2.Converter;
import retrofit2.Retrofit;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;

public class JsonPathConverterFactory extends Converter.Factory
{
	@Nullable
	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit)
	{
		var anno = (JsonPathBodyUnmarshal) Arrays.stream(annotations)
				.filter(a -> a.annotationType() == JsonPathBodyUnmarshal.class)
				.findAny().orElse(null);

		if (anno == null) return null;
		return (body) -> JsonUnmarshaller.unmarshal(body.string(), anno.value(), (Class<?>) type);
	}
}
