/*Copyright (C) 2015 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.io.checksum.impl;

import static ch.sourcepond.io.checksum.impl.DigestHelper.perform;
import static ch.sourcepond.io.checksum.impl.DigestHelper.performUpdate;
import static java.lang.Long.MAX_VALUE;
import static java.lang.Thread.sleep;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystem;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.security.MessageDigest;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.internal.matchers.VarargMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author rolandhauser
 *
 */
public class DigestHelperTest {
	private final InputStream in = mock(InputStream.class);
	private final Cancellable cancellable = mock(Cancellable.class);
	private final ScheduledExecutorService srv = newScheduledThreadPool(1);
	private final Runnable cancelTask = new Runnable() {

		@Override
		public void run() {
			when(cancellable.isCancelled()).thenReturn(true);
		}
	};
	private MessageDigest digest;

	/**
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		digest = MessageDigest.getInstance("SHA-256");
		when(in.read()).thenAnswer(new Answer<Integer>() {

			@Override
			public Integer answer(final InvocationOnMock invocation) throws Throwable {
				sleep(10);
				return 0;
			}
		});
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		srv.shutdown();
	}

	/**
	 * 
	 */
	@Test(timeout = 1000)
	public void verifyCancelDuringPerform() throws Exception {
		srv.schedule(cancelTask, 500, MILLISECONDS);
		assertNull(perform(digest, cancellable, in));
	}

	@SuppressWarnings("serial")
	private class FileAttrMatcher implements ArgumentMatcher<FileAttribute<?>>, VarargMatcher {

		@Override
		public boolean matches(final Object item) {
			return ((FileAttribute<?>[]) item).length == 0;
		}

		@Override
		public String toString() {
			return "Empty FileAttribute array";
		}
	}

	/**
	 * @throws Exception
	 */
	@Test(timeout = 2000)
	public void verifyCancelDuringPerformUpdated() throws Exception {
		final FileLock lock = mock(FileLock.class);
		final FileChannel ch = spy(FileChannel.class);
		final Path path = mock(Path.class);
		final FileSystem fs = mock(FileSystem.class);
		final FileSystemProvider provider = mock(FileSystemProvider.class);
		when(path.getFileSystem()).thenReturn(fs);
		when(fs.provider()).thenReturn(provider);

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
		final ByteBuffer buffer = ByteBuffer.allocate(100);
		srv.schedule(cancelTask, 500, MILLISECONDS);

		// Update should be cancelled after 500ms
		performUpdate(digest, cancellable, path, buffer);
	}
}