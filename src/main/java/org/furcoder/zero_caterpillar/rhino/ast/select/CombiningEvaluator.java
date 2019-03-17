package org.furcoder.zero_caterpillar.rhino.ast.select;

import org.mozilla.javascript.ast.AstNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class CombiningEvaluator implements Evaluator
{
	List<Evaluator> evaluators = new ArrayList<>();

	static class And extends CombiningEvaluator
	{
		@Override public String toString()					{ return evaluators.stream().map(Object::toString).collect(Collectors.joining()); }
		@Override public boolean matches(AstNode node)		{ return evaluators.stream().allMatch(p -> p.matches(node)); }
	}

	static class Or extends CombiningEvaluator
	{
		@Override public String toString()					{ return evaluators.stream().map(Object::toString).collect(Collectors.joining("|")); }
		@Override public boolean matches(AstNode node)		{ return evaluators.stream().anyMatch(p -> p.matches(node)); }
	}
}
