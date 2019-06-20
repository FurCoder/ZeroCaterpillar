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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.furcoder.zero_caterpillar.CaterpillarService;
import org.furcoder.zero_caterpillar.gui.Service;
import org.furcoder.zero_caterpillar.retrofit2.OkHttpService;
import org.furcoder.zero_caterpillar.retrofit2.RetrofitService;
import org.furcoder.zero_caterpillar.service.ServiceAnnotation;
import org.furcoder.zero_caterpillar.service.ServiceBase;
import org.furcoder.zero_caterpillar.util.SneakyThrowUtils;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

@ServiceAnnotation.Bind(value = PixivService.Config.class, type = { OkHttpService.Config.class, RetrofitService.Config.class })
@ServiceAnnotation.Bind(OkHttpService.class)
@ServiceAnnotation.Bind(RetrofitService.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service(name = "Pixiv")
public class PixivService extends CaterpillarService<PixivJob>
{
	public static class Config extends ServiceBase implements OkHttpService.Config, RetrofitService.Config
	{
		@Override public String baseUrl()	{ return PixivWebAPI.API_URL; }
		@Override public Consumer<CookieManager> cookieManagerHandler()
		{
			return (m) -> SneakyThrowUtils.wrap(() -> {
				var pixivSessionCookie = new HttpCookie("PHPSESSID", Files.readString(Path.of("pixiv_PHPSESSID")));
				pixivSessionCookie.setDomain(".pixiv.net");
				pixivSessionCookie.setPath("/");
				pixivSessionCookie.setHttpOnly(true);
				pixivSessionCookie.setSecure(true);
				m.getCookieStore().add(null, pixivSessionCookie);
			});
		}
	}


	@Getter(AccessLevel.PACKAGE)
	PixivWebService webService;


	@Override
	public void init()
	{
		var retrofitService = service(RetrofitService.class);
		webService = new PixivWebService(retrofitService.getRetrofit());
	}

	@Override
	public void destroy()
	{
		webService = null;
	}
}
