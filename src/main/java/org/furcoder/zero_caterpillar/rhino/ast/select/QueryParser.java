/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.furcoder.zero_caterpillar.rhino.ast.select;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.furcoder.zero_caterpillar.rhino.ast.select.StructuralEvaluator.ImmediateParent;
import org.furcoder.zero_caterpillar.rhino.ast.select.StructuralEvaluator.Parent;
import org.furcoder.zero_caterpillar.util.SneakyThrows;
import org.reflections.Reflections;

import java.util.List;
import java.util.ListIterator;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
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

	List<EvaluatorParser> parsers;
	static
	{
		parsers = new Reflections(Evaluator.class.getPackageName()).getSubTypesOf(Evaluator.ParsableEvaluator.class)
				.stream().sorted((a, b) -> {
					var priorityA = a.getAnnotation(Evaluator.Priority.class);
					var priorityB = b.getAnnotation(Evaluator.Priority.class);
					return (priorityB == null ?  0 : priorityB.value()) - (priorityA == null ?  0 : priorityA.value());
				})
				.map(e -> SneakyThrows.block(() -> (EvaluatorParser) e.getDeclaredField("parser").get(null)))
				.collect(Collectors.toUnmodifiableList());
	}

	Pattern pattern = Pattern.compile("([ >]*)([^\\s>]+)");
	AstQuery compile(String queryStr)
	{
		AstQuery query = new AstQuery();

		var subQueryStrs = queryStr.split(",");
		for (var str : subQueryStrs)
		{
			var matchResults = pattern.matcher(str).results().collect(Collectors.toList());
			Evaluator evaluator = compile(matchResults.listIterator(matchResults.size()));
			query.evaluators.add(evaluator);
		}
		return query;
	}

	Evaluator compile(ListIterator<MatchResult> iterator)
	{
		var matchResult = iterator.previous();

		String operation = matchResult.group(1).trim();
		String subQueryStr = matchResult.group(2);
		var evaluator = parseSubQuery(subQueryStr);

		if (iterator.hasPrevious())
		{
			var parentEval = compile(iterator);
			var parent = operation.equals(">") ? new ImmediateParent(parentEval) : new Parent(parentEval);
			evaluator = new CombiningEvaluator.And(evaluator, parent);
		}
		return evaluator;
	}

	Evaluator parseSubQuery(String queryStr)
	{
		var andEvaluator = new CombiningEvaluator.And();
		while (!queryStr.isEmpty())
		{
			final var str = queryStr;
			var parseResult = parsers.stream().map(p -> p.parse(str)).filter(r -> r.isSuccess()).findFirst().orElse(null);
			if (parseResult == null) break;

			andEvaluator.evaluators.add(parseResult.evaluator);
			queryStr = parseResult.next;
		}

		return (andEvaluator.evaluators.size() > 1) ? andEvaluator : andEvaluator.evaluators.get(0);
	}
}
