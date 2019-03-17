package org.furcoder.zero_caterpillar.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.Callable;

@UtilityClass
public class SneakyThrows
{
	public <T> T block(Callable<T> s)
	{
		try
		{
			return s.call();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
