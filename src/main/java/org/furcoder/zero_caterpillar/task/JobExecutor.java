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
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.furcoder.zero_caterpillar.service.ServiceBase;

import java.util.concurrent.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobExecutor extends ServiceBase
{
	public interface Config
	{
		default int threads()	{ return 10; }
	}


	final BlockingQueue<Runnable> workQueue = new PriorityBlockingQueue<>();
	ExecutorService executorService;

	@Getter
	Job runningJob;


	@Override
	public void init()
	{
		var config = service(Config.class);
		if (config == null) config = new Config() {};

		executorService = new ThreadPoolExecutor(0, config.threads(), 60L, TimeUnit.SECONDS, workQueue);
	}

	@Override
	public void destroy()
	{
		executorService.shutdownNow();
		executorService = null;
	}

	public void execute(Job job)
	{
		assert(runningJob == null);
		assert(job.getStatus() == Job.Status.PENDING);

		runningJob = job;
		job.begin(this);

		// onFinish
		executorService.execute(new ImmediateTask(Integer.MIN_VALUE) {
			@Override public float completionRate()	{ return Float.NaN; }
			@Override @SneakyThrows
			public void run()
			{
				while (workQueue.size() != 1 || workQueue.peek() != this) Thread.sleep(1000);

				runningJob.end(true);
				runningJob = null;
			}
		});
	}

	void submitTask(Task task)
	{
		assert(runningJob != null);
		assert(runningJob.getStatus() == Job.Status.RUNNING);

		executorService.execute(task);
	}
}
