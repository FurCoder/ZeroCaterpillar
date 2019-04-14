package org.furcoder.zero_caterpillar.util;

import java.util.Objects;

@FunctionalInterface
public interface ThrowingTriConsumer<T, U, V>
{
	void accept(T t, U u, V v) throws Throwable;

	default ThrowingTriConsumer<T, U, V> andThen(ThrowingTriConsumer<? super T, ? super U, ? super V> after) throws Throwable
	{
		Objects.requireNonNull(after);
		return (t, u, v) -> {
			accept(t, u, v);
			after.accept(t, u, v);
		};
	}
}
