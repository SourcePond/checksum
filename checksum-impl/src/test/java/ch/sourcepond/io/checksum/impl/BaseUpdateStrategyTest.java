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
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author rolandhauser
 *
 */
public class BaseUpdateStrategyTest {
	private static final Object SOURCE = new Object();

	/**
	 * @author rolandhauser
	 *
	 */
	private class TestBaseUpdateStrategy extends BaseUpdateStrategy<Object> {

		public TestBaseUpdateStrategy(final String pAlgorithm, final Object pSource) throws NoSuchAlgorithmException {
			super(pAlgorithm, pSource);
		}

		public TestBaseUpdateStrategy() throws NoSuchAlgorithmException {
			super(SHA256.toString(), SOURCE);
		}

		@Override
		protected void doUpdate(final long pInterval, final TimeUnit pUnit) throws IOException {
			// noop
		}
	}

	private static final byte[] ANY_DIGEST = new byte[] { 1, 2, 3 };
	private final ExecutorService executor = newCachedThreadPool();
	private final MessageDigest digest = mock(MessageDigest.class);
	private BaseUpdateStrategy<Object> strategy;

	@Before
	public void setup() throws Exception {
		strategy = spy(new TestBaseUpdateStrategy());
		when(strategy.getDigest()).thenReturn(digest);
	}

	@After
	public void tearDown() throws Exception {
		executor.shutdown();
	}

	@Test(expected = NullPointerException.class)
	public void verifyConstructor_AlgorithmIsNull() throws Exception {
		new TestBaseUpdateStrategy(null, SOURCE);
	}

	@Test(expected = NullPointerException.class)
	public void verifyConstructor_SourceIsNull() throws Exception {
		new TestBaseUpdateStrategy(SHA256.toString(), null);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void verifyConstructor_AlgorithmUnknown() throws Exception {
		new TestBaseUpdateStrategy("UNKNOWN", SOURCE);
	}

	/**
	 * 
	 */
	@Test
	public void verifyGetSource() {
		assertSame(SOURCE, strategy.getSource());
	}

	@Test
	public void verifyGetAlgorithm() {
		assertEquals(SHA256.toString(), strategy.getAlgorithm());
	}

	@Test(timeout = 100)
	public void verifyWait_Cancelled() throws Exception {
		final Future<?> f = executor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					synchronized (strategy) {
						strategy.wait(3, SECONDS);
					}
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		});
		synchronized (strategy) {
			strategy.cancel();
		}
		f.get();
	}

	@Test(timeout = 100)
	public void verifyWait_ZeroTimeOut() throws Exception {
		final Future<?> f = executor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					synchronized (strategy) {
						strategy.wait(0, SECONDS);
					}
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		});
		f.get();
	}

	@Test(timeout = 4000)
	public void verifyWait() throws Exception {
		final long startTimeMillis = currentTimeMillis();
		executor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					synchronized (strategy) {
						strategy.wait(1, SECONDS);
					}
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}).get();
		assertTrue(currentTimeMillis() >= (startTimeMillis + 1000));
	}

	@Test(timeout = 4000)
	public void verifyWait_ThreadInterrupted() throws Exception {
		final IOException expected = executor.submit(new Callable<IOException>() {

			@Override
			public IOException call() {
				try {
					currentThread().interrupt();
					synchronized (strategy) {
						strategy.wait(1, SECONDS);
					}
				} catch (final IOException e) {
					return e;
				}
				return null;
			}
		}).get();
		assertNotNull(expected);
		assertTrue(expected.getCause() instanceof InterruptedException);
	}

	/**
	 * 
	 */
	@Test
	public void verifyCancelUpdate() throws Exception {
		assertFalse(strategy.isCancelled());
		strategy.cancel();
		assertTrue(strategy.isCancelled());
		strategy.update(0, MILLISECONDS);
		assertFalse(strategy.isCancelled());
		strategy.update(0, MILLISECONDS);
		assertFalse(strategy.isCancelled());
	}

	@Test
	public void verifyDigest() throws Exception {
		when(digest.digest()).thenReturn(ANY_DIGEST);
		assertArrayEquals(ANY_DIGEST, strategy.digest());
	}

	@Test
	public void verifyDigest_UpdateCancelled() throws Exception {
		strategy.cancel();
		assertNull(strategy.digest());
	}

	@Test
	public void verifyCancel_ResetOnce() {
		strategy.cancel();
		strategy.cancel();
		verify(digest).reset();
	}

	@Test
	public void verifyFinalize() {
		strategy.finalize();
		verify(digest).reset();
	}
}
