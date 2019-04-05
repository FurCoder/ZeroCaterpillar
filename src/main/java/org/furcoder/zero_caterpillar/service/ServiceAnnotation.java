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

package org.furcoder.zero_caterpillar.service;

import java.lang.annotation.*;

public class ServiceAnnotation
{
	@Repeatable(Binds.class)
	@Documented
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Bind
	{
		Class<? extends ServiceBase> value();
		Class<?>[] type() default {};
	}

	@Documented
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Binds
	{
		Bind[] value();
	}

	@Repeatable(DependsOns.class)
	@Documented
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface DependsOn
	{
		Class<?> value();
	}

	@Documented
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface DependsOns
	{
		DependsOn[] value();
	}
}
