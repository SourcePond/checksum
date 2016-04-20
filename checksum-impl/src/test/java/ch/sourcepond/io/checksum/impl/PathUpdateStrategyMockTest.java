/*Copyright (C) 2016 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/package ch.sourcepond.io.checksum.impl;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static java.lang.Long.MAX_VALUE;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystem;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.internal.matchers.VarargMatcher;

/**
 * @author rolandhauser
 *
 */
public class PathUpdateStrategyMockTest {

	@SuppressWarnings("serial")
	private static class FileAttrMatcher implements ArgumentMatcher<FileAttribute<?>>, VarargMatcher {

		@Override
		public boolean matches(final Object item) {
			return ((FileAttribute<?>[]) item).length == 0;
		}

		@Override
		public String toString() {
			return "Empty FileAttribute array";
		}
	}

	public abstract static class TestFileChannel extends FileChannel {

		@Override
		public abstract void implCloseChannel() throws IOException;

	}

	private final ScheduledExecutorService executor = newSingleThreadScheduledExecutor();
	private final BasicFileAttributes fileAttr = mock(BasicFileAttributes.class);
	private final FileLock lock = mock(FileLock.class);
	private final TestFileChannel ch = spy(TestFileChannel.class);
	private final Path path = mock(Path.class);
	private final FileSystem fs = mock(FileSystem.class);
	private final FileSystemProvider provider = mock(FileSystemProvider.class);
	private PathUpdateStrategy strategy;

	/**
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		when(path.getFileSystem()).thenReturn(fs);
		when(fs.provider()).thenReturn(provider);
		when(provider.readAttributes(path, BasicFileAttributes.class)).thenReturn(fileAttr);
		when(provider.newFileChannel(Mockito.eq(path),
				Mockito.argThat(new ArgumentMatcher<Set<? extends OpenOption>>() {

					@SuppressWarnings("unchecked")
					@Override
					public boolean matches(final Object item) {
						final Set<? extends OpenOption> set = (Set<? extends OpenOption>) item;
						return set.contains(READ) && set.size() == 1;
					}

					@Override
					public String toString() {
						return "Set of size 1 and OpenOption 'READ'";
					}
				}), Mockito.argThat(new FileAttrMatcher()))).thenReturn(ch);
		when(ch.lock(0l, MAX_VALUE, true)).thenReturn(lock);
		strategy = new PathUpdateStrategy(SHA256.toString(), path);
	}

	/**
	 * @throws Exception
	 */
	@Test(timeout = 2000)
	public void verifyCancelDuringPerformUpdated() throws Exception {
		when(ch.read(strategy.getTempBuffer())).thenReturn(10);

		// Update should be cancelled after 500ms
		executor.schedule(new Runnable() {

			@Override
			public void run() {
				strategy.cancel();
			}
		}, 500, MILLISECONDS);

		// This will block until the calculation is done or the update has been
		// cancelled.
		strategy.update(0l, TimeUnit.MILLISECONDS);
		verify(ch).implCloseChannel();
		verify(lock).release();
	}

}
