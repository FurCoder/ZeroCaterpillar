package org.furcoder.zero_caterpillar.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class StringEscapeUtils extends org.apache.commons.text.StringEscapeUtils
{
	// Related issues:
	// https://issues.apache.org/jira/projects/TEXT/issues/TEXT-48
	public String hexEscapeToUnicodeEscape(String str)
	{
		return StringUtils.replace(str, "\\x", "\\u00");
	}
}
