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

package org.furcoder.zero_caterpillar.retrofit2;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.furcoder.zero_caterpillar.service.ServiceAnnotation;
import org.furcoder.zero_caterpillar.service.ServiceBase;

import java.net.CookieManager;
import java.util.function.Consumer;

@ServiceAnnotation.DependsOn(OkHttpService.Config.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OkHttpService extends ServiceBase
{
	public interface Config
	{
		default Consumer<CookieManager> cookieManagerHandler()	{ return null; }
	}


	@Getter
	OkHttpClient httpClient;


	@Override
	public void init()
	{
		var config = service(Config.class);

		CookieManager cookieManager = new CookieManager();
		if (config.cookieManagerHandler() != null) config.cookieManagerHandler().accept(cookieManager);

		httpClient = new OkHttpClient.Builder()
				.cookieJar(new JavaNetCookieJar(cookieManager))
				// ProgressCallback
				.addNetworkInterceptor(chain -> {
					var response = chain.proceed(chain.request());
					var listener = chain.request().tag(ProgressCallback.class);
					if (listener == null) return response;

					return response.newBuilder()
							.body(new ProgressResponseBody(response.body(), listener))
							.build();
				})
				.build();
	}

	@Override
	public void destroy()
	{

	}

	public DownloadRequest.Builder downloadRequest()
	{
		return DownloadRequest.builder().service(this);
	}
}
