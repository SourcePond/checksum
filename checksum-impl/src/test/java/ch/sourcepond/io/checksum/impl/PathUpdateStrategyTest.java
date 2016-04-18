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

import static java.lang.Long.MAX_VALUE;
import static java.lang.Thread.sleep;
import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.junit.Assert.assertEquals;
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
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
public class PathUpdateStrategyTest extends BaseUpdateStrategyTest<PathUpdateStrategy> {
	public static final String EXPECTED_HASH = "40ab41c711d6979c8bfb9dae2022d79e4fa43b79bf5c74cc8d291936586a4778";
	private static final String ALGORITHM = "SHA-256";
	private final Path file = getDefault().getPath(USER_DIR, "src", "test", "resources", "first_content.txt");
	private final InputStream in = mock(InputStream.class);
	private final ScheduledExecutorService srv = newScheduledThreadPool(1);
	private final Runnable cancelTask = new Runnable() {

		@Override
		public void run() {

		}
	};
	private MessageDigest digest;

	@Override
	protected PathUpdateStrategy newStrategy() throws NoSuchAlgorithmException {
		return new PathUpdateStrategy(ALGORITHM, file);
	}

	/**
	 * 
	 */
	@Test
	public void verifyUpdateDigest() throws Exception {
		strategy.update(0, MILLISECONDS);
		final byte[] result = strategy.digest();
		assertEquals(EXPECTED_HASH, encodeHexString(result));
	}

	/**
	 * 
	 */
	@Test
	public void verifyUpdateDigestGCRun() throws Exception {
		// After running the garbage collector the weak-references should have
		// been cleared.
		Runtime.getRuntime().gc();

		// This should not cause an exception; weak-references are initialized
		// with new values.
		strategy.update(0, MILLISECONDS);
		final byte[] result = strategy.digest();
		assertEquals(EXPECTED_HASH, encodeHexString(result));
	}

	/**
	 * @throws Exception
	 */
	@Override
	@Before
	public void setup() throws Exception {
		super.setup();
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
		strategy.update(0l, TimeUnit.MILLISECONDS);
	}
}
