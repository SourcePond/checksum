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

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static java.lang.Long.MAX_VALUE;
import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.apache.commons.lang3.SystemUtils.JAVA_IO_TMPDIR;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystem;
import java.nio.file.Files;
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
public class PathUpdateStrategyTest {

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

	/**
	 *
	 */
	private static class TestDataWriter implements Runnable {
		private final Path testFile;
		private final String data;

		public TestDataWriter(final Path pTestFile, final String pData) {
			testFile = pTestFile;
			data = pData;
		}

		@Override
		public void run() {
			try (final BufferedWriter wr = Files.newBufferedWriter(testFile, APPEND)) {
				wr.write(data);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static final String EXPECTED_HASH = "40ab41c711d6979c8bfb9dae2022d79e4fa43b79bf5c74cc8d291936586a4778";
	private final ScheduledExecutorService executor = newSingleThreadScheduledExecutor();
	private final BasicFileAttributes fileAttr = mock(BasicFileAttributes.class);
	private final FileLock lock = mock(FileLock.class);
	private final FileChannel ch = spy(FileChannel.class);
	private final Path path = mock(Path.class);
	private final FileSystem fs = mock(FileSystem.class);
	private final FileSystemProvider provider = mock(FileSystemProvider.class);
	private final Path realFile = getDefault().getPath(USER_DIR, "src", "test", "resources", "first_content.txt");
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
	 * 
	 */
	@Test
	public void verifyUpdateDigestWithRealFile() throws Exception {
		strategy = new PathUpdateStrategy(SHA256.toString(), realFile);
		strategy.update(0, MILLISECONDS);
		final byte[] result = strategy.digest();
		assertEquals(EXPECTED_HASH, encodeHexString(result));
	}

	/**
	 * 
	 */
	@Test(timeout = 2000)
	public void verifyUpdateDigestGCRun() throws Exception {
		strategy = new PathUpdateStrategy(SHA256.toString(), realFile);
		final long objectIdentityBeforeGC = System.identityHashCode(strategy.getTempBuffer());

		// Call to update will set the hard-reference tempBuffer to null. This
		// is necessary, otherwise the weak-reference will not be cleared.
		strategy.update(0, MILLISECONDS);

		// After running the garbage collector the weak-references should have
		// been cleared.
		System.gc();
		assertNotEquals(objectIdentityBeforeGC, System.identityHashCode(strategy.getTempBuffer()));
	}

	@Test
	public void verifyMoreContentWhileWaitingAfterEOF() throws Exception {
		final Path testFile = getDefault().getPath(JAVA_IO_TMPDIR, "morecontent.txt");
		deleteIfExists(testFile);
		createFile(testFile);
		try {
			strategy = new PathUpdateStrategy(SHA256.toString(), testFile);
			executor.schedule(new TestDataWriter(testFile, "abcdefg"), 500, MILLISECONDS);
			executor.schedule(new TestDataWriter(testFile, "hijklmn"), 1000, MILLISECONDS);
			executor.schedule(new TestDataWriter(testFile, "opqrstu"), 1500, MILLISECONDS);
			synchronized (strategy) {
				strategy.update(1000, MILLISECONDS);
			}
		} finally {
			deleteIfExists(testFile);
		}
		assertEquals("25f62a5a3d414ec6e20907df7f367f2b72625aade552db64c07933f6044fc49a",
				encodeHexString(strategy.digest()));
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
	}
}
