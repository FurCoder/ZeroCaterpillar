package org.furcoder.zero_caterpillar.util;

@FunctionalInterface
public interface TriFunction<T, U, V, R>
{
	R apply(T t, U u, V v);
}
