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
