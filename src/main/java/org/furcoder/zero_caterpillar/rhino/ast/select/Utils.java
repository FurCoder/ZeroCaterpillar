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
