package org.furcoder.zero_caterpillar.rhino.ast.select;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.furcoder.zero_caterpillar.util.SneakyThrows;
import org.reflections.Reflections;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class QueryParser
{
	@FieldDefaults(makeFinal = true)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	static class ParseResult
	{
		String next;
		Evaluator evaluator;

		boolean isSuccess()			{ return evaluator != null; }

		static ParseResult success(String next, Evaluator eval)	{ return new ParseResult(next, eval); }
		static ParseResult failed(String next)						{ return new ParseResult(next, null); }
	}

	interface EvaluatorParser
	{
		ParseResult parse(String query);
	}

	static List<EvaluatorParser> parser;
	static
	{
		parser = new Reflections(Evaluator.class.getPackageName()).getSubTypesOf(Evaluator.class)
				.stream().sorted((a, b) -> {
					var priorityA = a.getAnnotation(Evaluator.Priority.class);
					var priorityB = b.getAnnotation(Evaluator.Priority.class);
					return (priorityB == null ?  0 : priorityB.value()) - (priorityA == null ?  0 : priorityA.value());
				})
				.map(e -> SneakyThrows.block(() -> (EvaluatorParser) e.getDeclaredField("parser").get(null)))
				.collect(Collectors.toUnmodifiableList());
	}

	AstQuery compile(String query)
	{
		return null;
	}
}
