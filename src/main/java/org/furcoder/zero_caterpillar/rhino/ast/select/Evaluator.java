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
			if (!match.lookingAt()) return ParseResult.failed(str);

			var result = match.toMatchResult();
			str = new StringBuilder(str).delete(result.start(), result.end()).toString();
			return ParseResult.success(str, supplier.apply(result));
		};
	}

	interface ParsableEvaluator extends Evaluator {}

	// NodeType
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	class ByNodeType implements ParsableEvaluator
	{
		static Pattern pattern = Pattern.compile("^([a-zA-Z_$][0-9a-zA-Z_$]*)");
		static EvaluatorParser parser = defaultMatch(pattern, match -> new ByNodeType(match.group(1)));

		String type;

		@Override public String toString()					{ return type; }
		@Override public boolean matches(AstNode node)		{ return type.equals(node.shortName()); }
	}

	// #Name
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	class ByName implements ParsableEvaluator
	{
		static Pattern pattern = Pattern.compile("^#([a-zA-Z_$][0-9a-zA-Z_$]*)");
		static EvaluatorParser parser = defaultMatch(pattern, match -> new ByName(match.group(1)));

		String name;

		@Override public String toString()					{ return String.format("#%s", name); }
		@Override public boolean matches(AstNode node)		{ return name.equals(Utils.getName(node)); }
	}
}
