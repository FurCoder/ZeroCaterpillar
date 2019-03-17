package org.furcoder.zero_caterpillar.rhino.ast.select;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.furcoder.zero_caterpillar.rhino.ast.select.QueryParser.EvaluatorParser;
import org.furcoder.zero_caterpillar.rhino.ast.select.QueryParser.ParseResult;
import org.mozilla.javascript.ast.AstNode;

import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public interface Evaluator
{
	boolean matches(AstNode node);


	@interface Priority
	{
		int value() default 0;
	}


	static <T extends Evaluator> EvaluatorParser defaultMatch(Pattern pattern, Function<MatchResult, T> supplier)
	{
		return (str) ->
		{
			var match = pattern.matcher(str);
			if (!match.matches()) return ParseResult.failed(str);

			var result = match.toMatchResult();
			str = new StringBuilder(str).delete(result.start(), result.end()).toString();
			return ParseResult.success(str, supplier.apply(result));
		};
	}


	// NodeType
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	class ByNodeType implements Evaluator
	{
		static Pattern pattern = Pattern.compile("^([a-zA-Z_$][0-9a-zA-Z_$]*)");
		static EvaluatorParser parser = defaultMatch(pattern, match -> new ByNodeType(match.group(0)));

		String type;

		@Override public String toString()					{ return type; }
		@Override public boolean matches(AstNode node)		{ return type.equals(node.shortName()); }
	}

	// #Name
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	class ByName implements Evaluator
	{
		static Pattern pattern = Pattern.compile("^#([a-zA-Z_$][0-9a-zA-Z_$]*)");
		static EvaluatorParser parser = defaultMatch(pattern, match -> new ByNodeType(match.group(0)));

		String name;

		@Override public String toString()					{ return String.format("#%s", name); }
		@Override public boolean matches(AstNode node)		{ return name.equals(Utils.getName(node)); }
	}
}
