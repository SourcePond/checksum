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
import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.newInputStream;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.apache.commons.lang3.SystemUtils.JAVA_IO_TMPDIR;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.sourcepond.io.checksum.api.Algorithm;
import ch.sourcepond.io.checksum.api.StreamSource;

/**
 * @author rolandhauser
 *
 */
public class StreamSourceUpdateStrategyTest {
	private final InputStream stream = new ByteArrayInputStream("abcdefghijaklmnop".getBytes());
	private final InputStream mockStream = mock(InputStream.class);
	private final StreamSource source = mock(StreamSource.class);
	private final ScheduledExecutorService executor = newScheduledThreadPool(1);
	private final UpdateStrategyFactory strategyFactory = new UpdateStrategyFactory();
	private StreamSourceUpdateStrategy strategy;

	private StreamSourceUpdateStrategy newStrategy(final Algorithm pAlgorithm, final StreamSource pSource)
			throws NoSuchAlgorithmException {
		return (StreamSourceUpdateStrategy) strategyFactory.newStrategy(pAlgorithm.toString(), pSource);
	}

	@Before
	public void setup() throws Exception {
		when(source.openStream()).thenReturn(stream);
		strategy = newStrategy(SHA256, source);
	}

	@After
	public void tearDown() throws Exception {
		executor.shutdown();
	}

	@Test
	public void verifyUpdate() throws Exception {
		strategy.update(0, MILLISECONDS);
		final byte[] result = strategy.digest();
		assertEquals("94ac0d8cfdc9093ce4b1a9967d72d44e9c3cd931ff92da4f1fe77ac44c1654fd", encodeHexString(result));
	}

	@Test(timeout = 5000)
	public void verifyUpdate_Cancelled() throws Exception {
		when(source.openStream()).thenReturn(mockStream);
		strategy = newStrategy(SHA256, source);
		executor.schedule(new Runnable() {

			@Override
			public void run() {
				strategy.cancel();
			}
		}, 500, MILLISECONDS);
		strategy.update(0, MILLISECONDS);
	}

	@Test
	public void verifyMoreContentWhileWaitingAfterEOF() throws Exception {
		final Path testFile = getDefault().getPath(JAVA_IO_TMPDIR, "morecontent.txt");
		when(source.openStream()).thenAnswer(new Answer<InputStream>() {

			@Override
			public InputStream answer(final InvocationOnMock invocation) throws Throwable {
				return newInputStream(testFile);
			}
		});
		deleteIfExists(testFile);
		createFile(testFile);
		try {
			strategy = newStrategy(SHA256, source);
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
}
