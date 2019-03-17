package org.furcoder.zero_caterpillar.jsoup.retrofit2;

import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import retrofit2.Converter;
import retrofit2.Retrofit;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.Arrays;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class JsoupConverterFactory extends Converter.Factory
{
	@Documented
	@Target(METHOD)
	@Retention(RUNTIME)
	public @interface JsoupElement
	{
		String	value();
	}

	@Nullable
	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit)
	{
		var anno = (JsoupElement) Arrays.stream(annotations)
				.filter(a -> a.annotationType() == JsoupElement.class)
				.findAny().orElse(null);

		if (anno == null) return null;

		return new Converter<>() {
			@Nullable
			@Override
			public Object convert(ResponseBody body) throws IOException
			{
				Document document = Jsoup.parse(body.string());
				Elements elements = document.select(anno.value());

				if (type == Elements.class) return elements;
				if (type == String[].class) return elements.stream().map(e -> e.data()).toArray(String[]::new);

				if (elements.isEmpty()) return null;

				Element element = elements.get(0);
				if (type == Element.class) return element;
				if (type == String.class) return element.data();

				return null;
			}
		};
	}
}
