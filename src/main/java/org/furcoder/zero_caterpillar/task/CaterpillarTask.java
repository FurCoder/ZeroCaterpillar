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
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class CaterpillarTask extends Task
{
	@Getter @Setter
	boolean enabled;

	@Getter @Setter
	ZonedDateTime lastUpdate;

	@Getter @Setter
	ScheduleTimeProvider scheduleTimeProvider;

	@Getter
	transient Status status = Status.NONE;

	@Getter
	transient TemporalAmount runningTime;

	@Getter
	transient final Set<ImmediateTask> tasks = new HashSet<>();


	public float completionRate()
	{
		return (float) tasks.stream().mapToDouble(t -> t.completionRate()).filter(r -> !Double.isNaN(r)).average().getAsDouble();
	}

	public ZonedDateTime scheduledTime()
	{
		return (scheduleTimeProvider == null) ? null : scheduleTimeProvider.nextTime(lastUpdate);
	}


	enum Status
	{
		NONE,
		PENDING,
		RUNNING,
		DONE
	}
}
