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

package org.furcoder.zero_caterpillar.util;

import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.util.function.Function;

@UtilityClass
public class AnnotationUtils extends org.apache.commons.lang3.AnnotationUtils
{
	@SuppressWarnings("unchecked")
	public <T extends Annotation, U extends Annotation>
	boolean visit(Class<?> clazz, Class<T> annoType, Class<U> listType, Function<U, T[]> toList, Function<T, Boolean> visitor)
	{
		for (var anno : clazz.getAnnotations())
		{
			if (anno.annotationType() == listType)
			{
				for (var a : toList.apply((U) anno)) if (!visitor.apply(a)) return false;
			}
			else if (anno.annotationType() == annoType)
			{
				if (!visitor.apply((T) anno)) return false;
			}
		}
		return true;
	}
}
