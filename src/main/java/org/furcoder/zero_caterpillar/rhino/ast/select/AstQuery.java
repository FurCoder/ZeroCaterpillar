package org.furcoder.zero_caterpillar.rhino.ast.select;

import org.mozilla.javascript.ast.AstNode;

import java.util.HashSet;
import java.util.Set;

public class AstQuery extends CombiningEvaluator.Or
{
	public static Set<AstNode> select(AstNode node, String query)	{ return QueryParser.compile(query).select(node); }


	public Set<AstNode> select(AstNode node)
	{
		var nodes = new HashSet<AstNode>();
		node.visit(n -> {
			if (matches(n)) nodes.add(n);
			return true;
		});
		return nodes;
	}
}
