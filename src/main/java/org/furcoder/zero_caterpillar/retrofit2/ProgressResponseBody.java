package org.furcoder.zero_caterpillar.retrofit2;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

import java.io.IOException;

public class ProgressResponseBody extends ResponseBody
{
	final ResponseBody responseBody;
	final ProgressCallback progressListener;
	final BufferedSource bufferedSource;

	ProgressResponseBody(ResponseBody body, ProgressCallback listener)
	{
		responseBody = body;
		progressListener = listener;
		bufferedSource = Okio.buffer(new ForwardingSource(responseBody.source())
		{
			long totalBytesRead = 0L;

			@Override
			public long read(Buffer sink, long byteCount) throws IOException
			{
				long bytesRead = super.read(sink, byteCount);
				totalBytesRead += bytesRead == -1 ? 0 : bytesRead;
				progressListener.onUpdate(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
				return bytesRead;
			}
		});
	}

	@Override
	public MediaType contentType()
	{
		return responseBody.contentType();
	}

	@Override
	public long contentLength()
	{
		return responseBody.contentLength();
	}

	@Override
	public BufferedSource source()
	{
		return bufferedSource;
	}
}
