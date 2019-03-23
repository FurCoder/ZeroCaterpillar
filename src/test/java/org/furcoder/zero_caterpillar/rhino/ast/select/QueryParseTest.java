package org.furcoder.zero_caterpillar.rhino.ast.select;

import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

@NoArgsConstructor
public class QueryParseTest
{
	@Test
	public void toStringTest()
	{
		Consumer<String> test = (str) -> {
			var query = QueryParser.compile(str);
			assertEquals("toStringTest: " + str, str, query.toString());
		};

		test.accept("a#b c#d");
		test.accept("a#b>c#d");
		test.accept("a#b c#d>e");
	}
}
