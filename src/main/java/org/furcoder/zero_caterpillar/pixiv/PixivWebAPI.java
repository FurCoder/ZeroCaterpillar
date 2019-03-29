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
import lombok.AllArgsConstructor;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.Map;

import static org.furcoder.zero_caterpillar.jsoup.retrofit2.JsoupConverterFactory.JsoupElement;

public interface PixivWebAPI
{
	String API_URL = "https://www.pixiv.net";


	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	class ProfileBody
	{
		public final Map<Integer, Object> illusts;
		public final Map<Integer, Object> manga;
		public final Map<Integer, Object> novels;
		public final Map<Integer, Object> mangaSeries;
		public final Map<Integer, Object> novelSeries;
	}

	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	class Profile
	{
		public final String error;
		public final String message;
		public final ProfileBody body;
	}

	@GET("/ajax/user/{memberId}/profile/all")
	Call<Profile> user_profile_all(
			@Path("memberId") int memberId
	);

	@GET("/member_illust.php?mode=medium")
	@JsoupElement("head > script:not([src])")
	Call<String[]> member_illust_inline_scripts(
			@Query("illust_id") int illustId
	);
}
