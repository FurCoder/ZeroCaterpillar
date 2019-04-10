package org.furcoder.zero_caterpillar.retrofit2;

public interface ProgressCallback
{
	void onUpdate(long bytesRead, long contentLength, boolean done);
}
