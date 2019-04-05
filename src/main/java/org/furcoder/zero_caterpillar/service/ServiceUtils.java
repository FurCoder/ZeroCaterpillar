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

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.furcoder.zero_caterpillar.pixiv.PixivService;
import org.furcoder.zero_caterpillar.service.ServiceAnnotation.Bind;
import org.furcoder.zero_caterpillar.service.ServiceAnnotation.Binds;
import org.furcoder.zero_caterpillar.service.ServiceAnnotation.DependsOn;
import org.furcoder.zero_caterpillar.service.ServiceAnnotation.DependsOns;
import org.furcoder.zero_caterpillar.util.AnnotationUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class ServiceUtils
{
	@SneakyThrows
	public <T extends ServiceContainer> T instantiate(Class<T> containerClass)
	{
		Map<Class<?>, Class<? extends ServiceBase>> serviceClasses = new HashMap<>();
		AnnotationUtils.visit(containerClass, Bind.class, Binds.class, l -> l.value(), bind -> {
			for (var type : bind.type()) serviceClasses.put(type, bind.value());
			serviceClasses.put(bind.value(), bind.value());
			return true;
		});

		T instance = containerClass.getConstructor().newInstance();
		for (var clazz : serviceClasses.values()) if (!instance.services.containsKey(clazz))
		{
			ServiceBase service = clazz.getConstructor().newInstance();
			service.container = instance;
			instance.services.put(clazz, service);
		}
		for (var entry : serviceClasses.entrySet()) if (entry.getKey() != entry.getValue())
		{
			instance.services.put(entry.getKey(), instance.services.get(entry.getValue()));
		}

		Map<ServiceBase, Boolean> initializingServices = new HashMap<>();
		Function<ServiceBase, Boolean>[] initDeps = new Function[1];
		initDeps[0] = (service) -> {
			var initState = initializingServices.get(service);
			if (initState == Boolean.FALSE) return false;
			if (initState == null)
			{
				initializingServices.put(service, Boolean.FALSE);
				boolean success = AnnotationUtils.visit(service.getClass(), DependsOn.class, DependsOns.class, l -> l.value(), depAnno -> {
					var depType = serviceClasses.get(depAnno.value());
					if (depType == null) return false;

					var dep = service.service(depType);
					if (dep == null) return false;
					return initDeps[0].apply(dep);
				});

				if (!success) return false;

				service.Init();
				initializingServices.put(service, Boolean.TRUE);
			}
			return true;
		};

		for (var entry : instance.services.entrySet())
		{
			if (!initDeps[0].apply(entry.getValue())) return null;
		}

		instance.Init();
		return instance;
	}

	public static void main(String[] args) throws InterruptedException
	{
		var t = System.currentTimeMillis();
		var service = instantiate(PixivService.class);
		System.out.println(System.currentTimeMillis() - t);
	}
}
