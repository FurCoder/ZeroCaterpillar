package org.furcoder.zero_caterpillar.util;

import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;

@UtilityClass
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils
{
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T castOrNull(Class<T> clazz, Object obj)
	{
		if (!clazz.isInstance(obj)) return null;
		return (T) obj;
	}
}
