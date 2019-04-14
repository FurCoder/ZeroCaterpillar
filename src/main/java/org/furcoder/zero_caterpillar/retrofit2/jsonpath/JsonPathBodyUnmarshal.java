package org.furcoder.zero_caterpillar.retrofit2.jsonpath;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface JsonPathBodyUnmarshal
{
	String value() default "$";
}
