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

package org.furcoder.zero_caterpillar.task;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.atomic.AtomicLong;

@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class ImmediateTask extends Task implements Comparable<ImmediateTask>
{
	static final AtomicLong counter = new AtomicLong();


	@Getter
	long immediateTaskId = counter.getAndIncrement();


	@Override
	public abstract float completionRate();

	@Override
	public int compareTo(ImmediateTask t)
	{
		if (getPriority() != t.getPriority()) return getPriority() - t.getPriority();

		if (immediateTaskId > t.immediateTaskId) return -1;
		if (immediateTaskId < t.immediateTaskId) return 1;
		return 0;
	}
}