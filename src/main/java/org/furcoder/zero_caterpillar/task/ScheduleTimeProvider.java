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
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;

@SuppressWarnings("unused")
@FunctionalInterface
public interface ScheduleTimeProvider
{
	ZonedDateTime nextTime(ZonedDateTime lastUpdate);


	@FieldDefaults(level = AccessLevel.PUBLIC)
	class Outdating implements ScheduleTimeProvider
	{
		TemporalAmount timeToLive;

		@Override
		public ZonedDateTime nextTime(ZonedDateTime lastUpdate)
		{
			if (timeToLive == null) return null;
			if (lastUpdate == null) return ZonedDateTime.now();
			return lastUpdate.plus(timeToLive);
		}
	}

	@FieldDefaults(level = AccessLevel.PUBLIC)
	class FixedPeriod implements ScheduleTimeProvider
	{
		ZonedDateTime startTime;
		TemporalAmount period;

		@Override
		public ZonedDateTime nextTime(ZonedDateTime lastUpdate)
		{
			if (startTime == null || period == null) return null;

			var now = ZonedDateTime.now();
			while (startTime.isBefore(now)) startTime.plus(period);
			return startTime;
		}
	}
}
