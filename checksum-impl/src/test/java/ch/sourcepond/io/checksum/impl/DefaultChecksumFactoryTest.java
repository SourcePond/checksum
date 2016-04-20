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
import static ch.sourcepond.io.checksum.api.Algorithm.SHA384;
import static java.lang.String.format;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import ch.sourcepond.io.checksum.api.Algorithm;
import ch.sourcepond.io.checksum.api.ChecksumFactory;
import ch.sourcepond.io.checksum.api.StreamSource;

/**
 * @author rolandhauser
 *
 */
public class DefaultChecksumFactoryTest {
	private static final String ALGORITHM = Algorithm.SHA256.toString();
	// We make the legal algorithm SHA-384 illegal for testing;
	private static final String ILLEGAL_ALGORITHM = Algorithm.SHA384.toString();
	private static final String ERROR_MESSAGE = "Should never happen";
	private static final URL SOURCE_URL;

	static {
		try {
			SOURCE_URL = new URL("http://someUrl");
		} catch (final MalformedURLException e) {
			throw new InstantiationError(e.getMessage());
		}
	}

	private final ExecutorService updateExecutor = newFixedThreadPool(1);
	private final ExecutorService listenerExecutor = mock(ExecutorService.class);
	private final UpdateStrategyFactory strategyFactory = mock(UpdateStrategyFactory.class);
	private final UpdateStrategy streamSourceStrategy = mock(UpdateStrategy.class);
	private final UpdateStrategy pathStrategy = mock(UpdateStrategy.class);
	private final UpdateStrategy urlStrategy = mock(UpdateStrategy.class);
	private final StreamSource source = mock(StreamSource.class);
	private final Path path = mock(Path.class);
	private final ChecksumFactory checksumFactory = new DefaultChecksumFactory(updateExecutor, listenerExecutor,
			strategyFactory);
	private final ArgumentMatcher<StreamSource> urlMatcher = new ArgumentMatcher<StreamSource>() {

		@Override
		public boolean matches(final Object argument) {
			final StreamSource src = (StreamSource) argument;
			return (src instanceof UrlStreamSource) && SOURCE_URL.equals(((UrlStreamSource) src).getURL());
		}

		@Override
		public String toString() {
			return format("%s with URL %s", UrlStreamSource.class.getSimpleName(), SOURCE_URL);
		}
	};

	@Before
	public void setup() throws Exception {
		when(strategyFactory.newStrategy(ALGORITHM, source)).thenReturn(streamSourceStrategy);
		when(strategyFactory.newStrategy(ALGORITHM, path)).thenReturn(pathStrategy);
		when(strategyFactory.newStrategy(Mockito.eq(ALGORITHM), Mockito.argThat(urlMatcher))).thenReturn(urlStrategy);
		doThrow(new NoSuchAlgorithmException(ERROR_MESSAGE)).when(strategyFactory).newStrategy(ILLEGAL_ALGORITHM, path);
		doThrow(new NoSuchAlgorithmException(ERROR_MESSAGE)).when(strategyFactory).newStrategy(ILLEGAL_ALGORITHM,
				source);
		doThrow(new NoSuchAlgorithmException(ERROR_MESSAGE)).when(strategyFactory)
				.newStrategy(Mockito.eq(ILLEGAL_ALGORITHM), Mockito.argThat(urlMatcher));
	}

	@After
	public void tearDown() {
		updateExecutor.shutdown();
	}

	@Test
	public void createWithStringAlgorithm_StreamSource() throws Exception {
		checksumFactory.create(ALGORITHM, source).update();
		verify(streamSourceStrategy, timeout(500)).update(0, MILLISECONDS);
	}

	@Test
	public void createWithStringAlgorithm_Path() throws Exception {
		checksumFactory.create(ALGORITHM, path).update();
		verify(pathStrategy, timeout(500)).update(0, MILLISECONDS);
	}

	@Test
	public void createWithStringAlgorithm_Url() throws Exception {
		checksumFactory.create(ALGORITHM, SOURCE_URL).update();
		verify(urlStrategy, timeout(500)).update(0, MILLISECONDS);
	}

	@Test
	public void createWithAlgorithm_StreamSource() throws Exception {
		checksumFactory.create(SHA256, source).update();
		verify(streamSourceStrategy, timeout(500)).update(0, MILLISECONDS);
	}

	@Test
	public void createWithAlgorithm_Path() throws Exception {
		checksumFactory.create(SHA256, path).update();
		verify(pathStrategy, timeout(500)).update(0, MILLISECONDS);
	}

	@Test
	public void createWithAlgorithm_Url() throws Exception {
		checksumFactory.create(SHA256, SOURCE_URL).update();
		verify(urlStrategy, timeout(500)).update(0, MILLISECONDS);
	}

	@Test
	public void createWithIllegalAlgorithm_StreamSource_ShouldNeverHappen() throws Exception {
		try {
			checksumFactory.create(SHA384, source);
			fail("Error expected");
		} catch (final InstantiationError e) {
			assertEquals(ERROR_MESSAGE, e.getMessage());
		}
	}

	@Test
	public void createWithAlgorithm_Path_ShouldNeverHappen() throws Exception {
		try {
			checksumFactory.create(SHA384, path);
			fail("Error expected");
		} catch (final InstantiationError e) {
			assertEquals(ERROR_MESSAGE, e.getMessage());
		}
	}

	@Test
	public void createWithAlgorithm_Url_ShouldNeverHappen() throws Exception {
		try {
			checksumFactory.create(SHA384, SOURCE_URL);
			fail("Error expected");
		} catch (final InstantiationError e) {
			assertEquals(ERROR_MESSAGE, e.getMessage());
		}
	}
}
