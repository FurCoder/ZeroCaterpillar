package org.furcoder.zero_caterpillar.util;

import java.util.Objects;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U>
{
	void accept(T t, U u) throws Throwable;

	default ThrowingBiConsumer<T, U> andThen(ThrowingBiConsumer<? super T, ? super U> after)
	{
		Objects.requireNonNull(after);

		return (l, r) -> {
			accept(l, r);
			after.accept(l, r);
		};
	}
}
