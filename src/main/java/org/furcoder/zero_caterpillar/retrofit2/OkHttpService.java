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
	public void Init()
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
	public void Destroy()
	{

	}
}
