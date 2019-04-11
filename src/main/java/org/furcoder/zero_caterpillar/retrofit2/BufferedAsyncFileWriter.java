package org.furcoder.zero_caterpillar.retrofit2;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BufferedAsyncFileWriter implements Closeable, Flushable
{
	final AsynchronousFileChannel fileChannel;
	final List<Future<Integer>> futures = new LinkedList<>();

	ByteBuffer buffer;

	@Getter
	int position = 0;


	public BufferedAsyncFileWriter(Path path, OpenOption... opts) throws IOException
	{
		fileChannel = AsynchronousFileChannel.open(path, opts);
	}

	@Override
	@SneakyThrows
	public void close() throws IOException
	{
		flush();
		join();
		fileChannel.close();
	}

	@Override
	public void flush() throws IOException
	{
		if (buffer == null) return;

		buffer.flip();
		futures.add(fileChannel.write(buffer, position));
		position += buffer.limit();
		buffer = null;
	}

	public void join() throws ExecutionException, InterruptedException, IOException
	{
		flush();
		for (var future : futures) future.get();
	}

	public void write(InputStream input) throws IOException
	{
		while (true)
		{
			if (buffer == null) buffer = ByteBuffer.allocate(256*1024);		// 256KB
			int len = input.read(buffer.array(), buffer.position(), buffer.remaining());
			if (len == -1) return;

			buffer.position(buffer.position() + len);
			if (!buffer.hasRemaining()) flush();
		}
	}
}
