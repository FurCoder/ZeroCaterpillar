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
