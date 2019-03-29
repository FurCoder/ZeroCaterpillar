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

import lombok.AllArgsConstructor;
import org.mozilla.javascript.ast.AstNode;

@SuppressWarnings("unused")
public interface StructuralEvaluator extends Evaluator
{
	@AllArgsConstructor
	class ImmediateParent implements StructuralEvaluator
	{
		Evaluator evaluator;

		@Override public String toString()					{ return String.format("%s>", evaluator.toString()); }
		@Override public boolean matches(AstNode node)
		{
			var parent = node.getParent();
			return parent != null ? evaluator.matches(parent) : false;
		}
	}

	@AllArgsConstructor
	class Parent implements StructuralEvaluator
	{
		Evaluator evaluator;

		@Override public String toString()					{ return String.format("%s ", evaluator.toString()); }
		@Override public boolean matches(AstNode node)
		{
			while (true)
			{
				node = node.getParent();
				if (node == null) return false;
				if (evaluator.matches(node)) return true;
			}
		}
	}
}
