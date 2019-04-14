package org.furcoder.zero_caterpillar.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;

@UtilityClass
public class ArrayUtils extends org.apache.commons.lang3.ArrayUtils
{
	public int[] addAll(int[]... arrays)
	{
		int length = 0;
		for (var array : arrays) length += array.length;
		final int[] joinedArray = new int[length];

		int pos = 0;
		for (var array : arrays)
		{
			System.arraycopy(array, 0, joinedArray, pos, array.length);
			pos += array.length;
		}
		return joinedArray;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] addAll(T[]... arrays)
	{
		final Class<?> type = arrays[0].getClass().getComponentType();

		int length = 0;
		for (var array : arrays) length += array.length;
		final T[] joinedArray = (T[]) Array.newInstance(type, length);

		int pos = 0;
		for (var array : arrays)
		{
			System.arraycopy(array, 0, joinedArray, pos, array.length);
			pos += array.length;
		}
		return joinedArray;
	}
}
