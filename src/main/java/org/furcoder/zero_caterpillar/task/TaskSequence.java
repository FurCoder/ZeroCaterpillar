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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskSequence implements Task.Cancelable
{
	List<Task> tasks = new ArrayList<>();
	Iterator<Task> iterator = null;

	Runnable runningTask = null;;
	boolean canceled;


	@Override
	public float completionRate()
	{
		return (float) tasks.stream().mapToDouble(t -> t.completionRate()).filter(r -> !Double.isNaN(r)).average().getAsDouble();
	}

	@Override
	public void cancel()
	{
		canceled = true;
		if (runningTask instanceof Cancelable) ((Cancelable) runningTask).cancel();
	}

	@Override
	public void run()
	{
		iterator = tasks.iterator();
		while (iterator.hasNext() && !canceled)
		{
			runningTask = iterator.next();
			runningTask.run();
		}
		runningTask = null;
	}

	public boolean isRunning()
	{
		return runningTask != null;
	}

	public void addTask(Task task)
	{
		tasks.add(task);
	}
}
