package org.furcoder.zero_caterpillar.rhino.ast.select;

import lombok.experimental.UtilityClass;
import org.furcoder.zero_caterpillar.util.ObjectUtils;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.VariableInitializer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class Utils
{
	Map<Class<? extends AstNode>, Function<? extends AstNode, String>> nameSuppliers = new HashMap<>();
	<T extends AstNode> void nameSupplier(Class<T> clz, Function<T, String> s) { nameSuppliers.put(clz, s); }
	static
	{
		nameSupplier(Name.class, (node) -> {
			var name = ObjectUtils.castOrNull(Name.class, node);
			return (name == null) ? null : name.getIdentifier();
		});
		nameSupplier(VariableInitializer.class,	(node) -> getName(node.getTarget()));
		nameSupplier(ObjectProperty.class,			(node) -> getName(node.getLeft()));
	}

	@SuppressWarnings("unchecked")
	@Nullable
	String getName(AstNode node)
	{
		var supplier = (Function<AstNode, String>) nameSuppliers.get(node.getClass());
		if (supplier == null) return null;
		return supplier.apply(node);
	}
}
