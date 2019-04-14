package org.furcoder.zero_caterpillar.jsonpath;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JsonPathProperty
{
	String value() default "";
}
