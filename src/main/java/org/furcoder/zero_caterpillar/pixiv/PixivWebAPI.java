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
import lombok.experimental.FieldDefaults;
import org.furcoder.zero_caterpillar.retrofit2.jsoup.JsoupElement;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

public interface PixivWebAPI
{
	String API_URL = "https://www.pixiv.net";


	@AllArgsConstructor
	@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
	class ProfileBody
	{
		Map<Integer, Object> illusts;
		Map<Integer, Object> manga;
		Map<Integer, Object> novels;
		Map<Integer, Object> mangaSeries;
		Map<Integer, Object> novelSeries;
	}

	@AllArgsConstructor
	@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
	class Profile
	{
		String error;
		String message;
		ProfileBody body;
	}

	@GET("/ajax/user/{member_id}/profile/all")
	Call<Profile> user_profile_all(
			@Path("member_id") int memberId
	);


	@GET("/member_illust.php?mode=medium")
	@JsoupElement("head > script:not([src])")
	Call<String[]> member_illust_inline_scripts(
			@Query("illust_id") int illustId
	);


	@AllArgsConstructor
	@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
	class IllustPageBodyUrls
	{
		String original;
	}

	@AllArgsConstructor
	@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
	class IllustPageBody
	{
		int height;
		int width;
		IllustPageBodyUrls urls;
	}

	@AllArgsConstructor
	@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
	class IllustPage
	{
		List<IllustPageBody> body;
	}

	@GET("/ajax/illust/{illust_id}/pages")
	Call<IllustPage> ajax_illust_pages(
			@Path("illust_id") int illustId
	);
}
