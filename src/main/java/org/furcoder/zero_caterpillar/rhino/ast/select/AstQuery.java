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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class AstQuery extends CombiningEvaluator.Or
{
	public static Set<AstNode> select(AstNode node, String query)	{ return QueryParser.compile(query).select(node); }
	public static void select(AstNode node, String query, Predicate<AstNode> visitor)	{ QueryParser.compile(query).select(node, visitor); }

	public static AstNode selectAny(AstNode node, String query)		{ return QueryParser.compile(query).selectAny(node); }


	public Set<AstNode> select(AstNode node)
	{
		var nodes = new HashSet<AstNode>();
		node.visit(n -> {
			if (matches(n)) nodes.add(n);
			return true;
		});
		return nodes;
	}

	public void select(AstNode node, Predicate<AstNode> visitor)
	{
		node.visit(n -> {
			if (matches(n)) return visitor.test(n);
			return true;
		});
	}

	public AstNode selectAny(AstNode node)
	{
		var nodes = new AstNode[1];
		node.visit(n -> {
			if (!matches(n)) return true;
			nodes[0] = n;
			return false;
		});
		return nodes[0];
	}
}
