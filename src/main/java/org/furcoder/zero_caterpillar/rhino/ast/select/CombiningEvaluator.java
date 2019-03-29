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

import org.mozilla.javascript.ast.AstNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class CombiningEvaluator implements Evaluator
{
	List<Evaluator> evaluators = new ArrayList<>();

	static class And extends CombiningEvaluator
	{
		public And() {}
		public And(Evaluator... evals) { evaluators = Arrays.asList(evals); }

		@Override public String toString()
		{
			return evaluators.stream()
					.sorted((l, r) -> {
						if (l instanceof StructuralEvaluator) return -1;
						if (r instanceof StructuralEvaluator) return 1;
						return 0;
					})
					.map(Object::toString)
					.collect(Collectors.joining());
		}
		@Override public boolean matches(AstNode node)		{ return evaluators.stream().allMatch(p -> p.matches(node)); }
	}

	static class Or extends CombiningEvaluator
	{
		public Or() {}
		public Or(Evaluator... evals) { evaluators = Arrays.asList(evals); }

		@Override public String toString()					{ return evaluators.stream().map(Object::toString).collect(Collectors.joining(",")); }
		@Override public boolean matches(AstNode node)		{ return evaluators.stream().anyMatch(p -> p.matches(node)); }
	}
}
