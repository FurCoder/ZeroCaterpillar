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
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.furcoder.zero_caterpillar.jsonpath.JsonPathProperty;
import org.furcoder.zero_caterpillar.jsonpath.JsonUnmarshaller;
import org.furcoder.zero_caterpillar.rhino.ast.select.AstQuery;
import org.furcoder.zero_caterpillar.util.StringEscapeUtils;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ObjectProperty;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PixivWebService
{
	static class Illust
	{
		int id;

		@JsonPathProperty("illustTitle")
		String title;

		@JsonPathProperty("illustComment")
		String comment;

		@JsonPathProperty("userIllusts.{0}.pageCount")
		int pages = 1;

		@JsonPathProperty("urls.original")
		void setUrl(String url) { urls = List.of(url); }
		List<String> urls;


		static Illust parsePreload(String jsonStr, int illustId)
		{
			var illust = JsonUnmarshaller.unmarshal(jsonStr, "$.illust.{0}", Illust.class, Integer.toString(illustId));
			illust.id = illustId;
			return illust;
		}

		@Override
		public String toString()
		{
			return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
		}
		
		public String referer()
		{
			return PixivWebAPI.API_URL + "/member_illust.php?mode=medium&illust_id=" + id;
		}
	}


	final PixivWebAPI api;


	public PixivWebService(Retrofit retrofit)
	{
		api = retrofit.create(PixivWebAPI.class);
	}

	private PixivWebAPI.ProfileBody getProfile(int memberId) throws IOException
	{
		var call = api.user_profile_all(memberId);
		var response = call.execute();

		if (!response.isSuccessful()) return null;
		return response.body().body;
	}

	@SneakyThrows
	public List<Integer> getIllustIds(int memberId)
	{
		var profile = getProfile(memberId);
		if (profile == null) return null;

		List<Integer> list = new ArrayList<>(profile.illusts.size() + profile.manga.size());
		list.addAll(profile.illusts.keySet());
		list.addAll(profile.manga.keySet());
		return list;
	}

	@SneakyThrows
	public Illust getIllust(int illustId)
	{
		var call = api.member_illust_inline_scripts(illustId);
		var response = call.execute();
		if (!response.isSuccessful()) return null;

		ObjectProperty preload = null;
		for (String script : response.body())
		{
			AstRoot astRoot = new Parser().parse(script, "", 0);
			var node = AstQuery.selectAny(astRoot, "VariableInitializer#globalInitData ObjectProperty#preload");
			if (node != null) preload = (ObjectProperty) node;
		}

		if (preload == null) return null;
		var jsonStr = StringEscapeUtils.hexEscapeToUnicodeEscape(preload.getRight().toSource());

		var illust = Illust.parsePreload(jsonStr, illustId);
		if (illust.pages > 1) illust.urls = getIllustUrls(illustId);
		return illust;
	}

	@SneakyThrows
	public List<String> getIllustUrls(int illustId)
	{
		var call = api.ajax_illust_pages(illustId);
		var response = call.execute();
		if (!response.isSuccessful()) return null;
		return response.body().body.stream().map(body -> body.urls.original).collect(Collectors.toList());
	}
}
