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
				if (bytesRead != 0) progressListener.onUpdate(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
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
