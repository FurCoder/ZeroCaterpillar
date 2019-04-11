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

package org.furcoder.zero_caterpillar.pixiv;

import lombok.AllArgsConstructor;
import org.furcoder.zero_caterpillar.retrofit2.OkHttpService;
import org.furcoder.zero_caterpillar.task.ImmediateTask;
import org.furcoder.zero_caterpillar.task.Task;
import org.furcoder.zero_caterpillar.util.SneakyThrows;

@AllArgsConstructor
public class IllustDownloadTask extends ImmediateTask
{
	public final PixivWebService service;
	public final OkHttpService httpService;
	public final int illustId;


	@Override
	public void run()
	{
		addTask(Task.create(() -> {
			var illust = service.getIllust(illustId);
			for (int i=0; i<illust.urls.size(); i++)
			{
				var filename = (illust.urls.size() == 1) ? ("test/" + id + ".jpg") : ("test/" + id + "_" + i + ".jpg");
				var downloadRequest = httpService.downloadRequest()
						.url(illust.urls.get(i))
						.referer(illust.referer())
						.build();

				addTask(Task.create(() -> downloadRequest.completionRate(), () -> {
					SneakyThrows.action(() -> downloadRequest.download(filename));
				}));
			}
		}));
	}
}
