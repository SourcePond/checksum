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

import static ch.sourcepond.io.checksum.impl.DefaultChecksum.INITIAL;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.io.checksum.api.ChecksumException;

/**
 * @author rolandhauser
 *
 */
public class DefaultChecksumTest {
	/**
	 * @author rolandhauser
	 *
	 */
	private class InterruptTest implements Runnable {
		private volatile boolean fail;
		private volatile Exception exception;

		@Override
		public void run() {
			try {
				checksum.update();
				currentThread().interrupt();
				checksum.getValue();
				fail = true;
			} catch (final Exception e) {
				exception = e;
			}
		}
	}

	private static final byte[] SECOND_VALUE = new byte[] { 98, 49, 53, 50 };
	private final UpdateStrategy strategy = mock(UpdateStrategy.class);
	private final ScheduledExecutorService delegate = newScheduledThreadPool(1);
	private final Executor updateExecutor = new Executor() {

		@Override
		public void execute(final Runnable command) {
			// This should cause the checksum methods to wait until update is
			// done.
			delegate.schedule(command, 500, TimeUnit.MILLISECONDS);
		}
	};
	private final ExecutorService listenerExecutor = Executors.newFixedThreadPool(1);

	private static final String ANY_ALGORITHM = "anyAlgorith";
	private static final String HEX_VALUE = "01030305";
	private static final byte[] VALUE = new byte[] { 1, 3, 3, 5 };
	private final DefaultChecksum checksum = new DefaultChecksum(strategy, updateExecutor, listenerExecutor);

	@Before
	public void setup() throws Exception {
		when(strategy.getAlgorithm()).thenReturn(ANY_ALGORITHM);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyIsUpdating() throws Exception {
		assertSame(checksum, checksum.update());
		assertTrue(checksum.isUpdating());
		sleep(1000);
		assertFalse(checksum.isUpdating());
	}

	/**
	 * 
	 */
	@Test
	public void verifyGetAlgorithm() {
		assertEquals(ANY_ALGORITHM, checksum.getAlgorithm());
	}

	/**
	 * 
	 */
	@Test
	public void verifyCopyArrayGetValue() throws Exception {
		when(strategy.digest()).thenReturn(VALUE);
		checksum.update();
		final byte[] copy = checksum.getValue();
		assertNotSame(VALUE, copy);
		assertArrayEquals(VALUE, copy);
	}

	/**
	 * 
	 */
	@Test
	public void verifyCopyArrayGetPreviousValue() throws Exception {
		when(strategy.digest()).thenReturn(VALUE).thenReturn(SECOND_VALUE);
		checksum.update().getValue();
		checksum.update().getValue();
		final byte[] copy = checksum.getPreviousValue();
		assertNotSame(VALUE, copy);
		assertArrayEquals(VALUE, copy);
	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		delegate.shutdown();
	}

	/**
	 * 
	 */
	@Test
	public void verifyCancel() {
		assertSame(checksum, checksum.cancel());
		verify(strategy).cancel();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyInitialValue() throws Exception {
		assertArrayEquals(INITIAL, checksum.getValue());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyInitialHexValue() throws Exception {
		assertEquals(EMPTY, checksum.getHexValue());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyInitialPreviousValue() throws Exception {
		assertArrayEquals(INITIAL, checksum.getPreviousValue());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyInitialPreviousHexValue() throws Exception {
		assertEquals(EMPTY, checksum.getPreviousHexValue());
	}

	/**
	 * 
	 */
	@Test
	public void verifyGetHexValue() throws Exception {
		when(strategy.digest()).thenReturn(VALUE).thenReturn(SECOND_VALUE);
		checksum.update();
		assertEquals(HEX_VALUE, checksum.getHexValue());
		checksum.update();
		assertEquals("62313532", checksum.getHexValue());
	}

	/**
	 * 
	 */
	@Test
	public void verifyGetPreviousHexValue() throws Exception {
		when(strategy.digest()).thenReturn(VALUE).thenReturn(SECOND_VALUE);
		checksum.update().getValue();
		checksum.update().getValue();
		assertEquals(HEX_VALUE, checksum.getPreviousHexValue());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyEqualsPrevious() throws Exception {
		when(strategy.digest()).thenReturn(VALUE).thenReturn(SECOND_VALUE).thenReturn(VALUE).thenReturn(VALUE);
		checksum.update();
		assertFalse(checksum.equalsPrevious());
		checksum.update();
		assertFalse(checksum.equalsPrevious());
		checksum.update();
		assertFalse(checksum.equalsPrevious());
		checksum.update();
		assertTrue(checksum.equalsPrevious());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyGetValue() throws Exception {
		when(strategy.digest()).thenReturn(VALUE);
		checksum.update();
		assertArrayEquals(VALUE, checksum.getValue());
	}

	/**
	 * 
	 */
	@Test
	public void verifyGetPreviousValue() throws Exception {
		when(strategy.digest()).thenReturn(VALUE).thenReturn(SECOND_VALUE);
		checksum.update().getValue();
		checksum.update().getValue();
		assertArrayEquals(VALUE, checksum.getPreviousValue());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyGetValueIOExceptionOccurred() throws Exception {
		final IOException expected = new IOException();
		doThrow(expected).when(strategy).update(0, MILLISECONDS);
		checksum.update();

		try {
			checksum.getValue();
			fail("Exception expected here");
		} catch (final ChecksumException e) {
			assertSame(expected, e.getCause());
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyGetValueRuntimeExceptionOccurred() throws Exception {
		final RuntimeException expected = new RuntimeException();
		doThrow(expected).when(strategy).update(0, MILLISECONDS);
		checksum.update();

		try {
			checksum.getValue();
			fail("Exception expected here");
		} catch (final ChecksumException e) {
			assertSame(expected, e.getCause());
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyDoNotCatchError() throws Exception {
		final Error expected = new Error();
		doThrow(expected).when(strategy).update(0, MILLISECONDS);
		checksum.update();

		try {
			checksum.getValue();
			fail("Exception expected here");
		} catch (final Error e) {
			assertSame(expected, e);
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyPropagateInterruptedException() throws Exception {
		final InterruptTest r = new InterruptTest();
		final Thread th = new Thread(r);
		th.start();
		th.join();

		assertFalse("Exception expected", r.fail);
		assertEquals(InterruptedException.class, r.exception.getCause().getClass());
	}
}
