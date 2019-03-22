package org.furcoder.zero_caterpillar.pixiv;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.furcoder.zero_caterpillar.jsoup.retrofit2.JsoupConverterFactory;
import org.furcoder.zero_caterpillar.rhino.ast.select.AstQuery;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.FileInputStream;
import java.net.CookieManager;
import java.net.HttpCookie;

public final class PixivService
{
	public static void main(String... args) throws Throwable
	{
		String pixivSessionId = new String(new FileInputStream("pixiv_PHPSESSID").readAllBytes());

		var pixivSessionCookie = new HttpCookie("PHPSESSID", pixivSessionId);
		pixivSessionCookie.setDomain(".pixiv.net");
		pixivSessionCookie.setPath("/");
		pixivSessionCookie.setHttpOnly(true);
		pixivSessionCookie.setSecure(true);

		CookieManager cookieManager = new CookieManager();
		cookieManager.getCookieStore().add(null, pixivSessionCookie);

		OkHttpClient httpClient = new OkHttpClient.Builder()
				.cookieJar(new JavaNetCookieJar(cookieManager))
				.build();

		Retrofit retrofit = new Retrofit.Builder()
				.client(httpClient)
				.baseUrl(PixivWebAPI.API_URL)
				.addConverterFactory(new JsoupConverterFactory())
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		PixivWebAPI pixivWebAPI = retrofit.create(PixivWebAPI.class);

		int illustId = 0;
		{
			var call = pixivWebAPI.user_profile_all(315442);
			var response = call.execute();

			if (response.isSuccessful())
			{
				var profile = response.body();
				var illustIds = profile.body.illusts.keySet();
				illustId = illustIds.iterator().next();
				for (var id : illustIds) System.out.println(id);
			}
		}

		if (illustId != 0)
		{
			var call = pixivWebAPI.member_illust_inline_scripts(illustId);
			var response = call.execute();

			if (response.isSuccessful())
			{
				AstNode preload = null;
				for (String script : response.body())
				{
					AstRoot astRoot = new Parser().parse(script, "", 0);
					var nodes = AstQuery.select(astRoot, "VariableInitializer#globalInitData ObjectProperty#preload");
					if (!nodes.isEmpty()) preload = nodes.iterator().next();
				}

				if (preload != null) System.out.println(preload.toSource());
			}
		}
	}
}