package org.furcoder.zero_caterpillar.retrofit2;

import lombok.Builder;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Value
@Builder(builderClassName = "Builder")
public class DownloadRequest
{
	OkHttpService service;
	String url;
	String referer;

	@NonFinal
	ProgressCallback callback;

	@Getter
	@NonFinal
	long contentLength;

	@Getter @NonFinal
	long bytesRead;


	private Response executeRequest() throws IOException
	{
		if (callback == null) callback = (bytesRead, contentLength, done) -> {
			this.contentLength = contentLength;
			this.bytesRead = bytesRead;
		};

		var requestBuilder = new Request.Builder();
		requestBuilder.url(url);
		if (referer != null) requestBuilder.header("Referer", referer);
		if (callback != null) requestBuilder.tag(ProgressCallback.class, callback);
		var request = requestBuilder.build();

		var response = service.getHttpClient().newCall(request).execute();
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		return response;
	}

	public byte[] download() throws IOException
	{
		@Cleanup var response = executeRequest();
		return response.body().bytes();
	}

	public void download(String filename) throws IOException
	{
		@Cleanup var response = executeRequest();
		var writer = new BufferedAsyncFileWriter(Path.of(filename), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		writer.write(response.body().byteStream());
		writer.close();
	}

	public float completionRate()
	{
		return (float) bytesRead / contentLength;
	}
}
