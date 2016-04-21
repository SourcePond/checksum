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

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.ChecksumException;
import ch.sourcepond.io.checksum.api.UpdateObserver;

/**
 * @author rolandhauser
 *
 */
public class ConcurrencyTest {
	private static class WaitFor implements Answer<Object> {
		private final long milliseconds;

		private WaitFor(final long pMilliseconds) {
			milliseconds = pMilliseconds;
		}

		@Override
		public Object answer(final InvocationOnMock invocation) throws Throwable {
			sleep(milliseconds);
			return null;
		}

	}

	private static final byte[] RESULT_1 = new byte[] { 90, 23, 13, 31, 99, 101 };
	private static final byte[] RESULT_2 = new byte[] { 82, 12, 45, 69, 102, 98 };
	private final UpdateObserver observer = mock(UpdateObserver.class);
	private final UpdateStrategy strategy = mock(UpdateStrategy.class);
	private final ExecutorService executor = newCachedThreadPool();
	private final Checksum chksum = new DefaultChecksum(strategy, executor, executor);

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyNoUpdateNecessary() throws Exception {
		chksum.getValue();
		verifyZeroInteractions(strategy, observer);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyIsUpdating() throws Exception {
		assertFalse(chksum.isUpdating());
		doAnswer(new WaitFor(500)).when(strategy).update(500, MILLISECONDS);
		doAnswer(new WaitFor(500)).when(strategy).update(500, MILLISECONDS);
		chksum.update().update();
		assertTrue(chksum.isUpdating());
		assertTrue(chksum.isUpdating());
		sleep(2000);
		assertFalse(chksum.isUpdating());
	}

	/**
	 * 
	 */
	@Test
	public void verifyAllowExactlyOneWaitingUpdate() throws Exception {
		doAnswer(new WaitFor(1000)).when(strategy).update(1000, MILLISECONDS);
		doAnswer(new WaitFor(500)).when(strategy).update(500, MILLISECONDS);
		doThrow(AssertionError.class).when(strategy).update(0, MILLISECONDS);
		when(strategy.digest()).thenReturn(RESULT_1);
		when(strategy.digest()).thenReturn(RESULT_2);

		// Call update 3 times;
		chksum.update(1000).update(500);
		assertArrayEquals(RESULT_2, chksum.update(0).getValue());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyInterruptFlagWhenAwaitCalcuationInterrupted() throws Throwable {
		final Callable<ChecksumException> test = new Callable<ChecksumException>() {

			@Override
			public ChecksumException call() throws Exception {
				doAnswer(new Answer<Object>() {

					@Override
					public Object answer(final InvocationOnMock invocation) throws Throwable {
						sleep(100);
						return null;
					}

				}).when(strategy).update(0, MILLISECONDS);
				currentThread().interrupt();

				ChecksumException expected = null;
				try {
					chksum.update().getValue();
					fail("Exception expected!");
				} catch (final ChecksumException e) {
					expected = e;
				}
				return expected;
			}
		};

		ChecksumException expected = null;
		try {
			expected = executor.submit(test).get();
		} catch (final ExecutionException e) {
			if (!(e.getCause() instanceof ChecksumException)) {
				throw e.getCause();
			}
		}
		assertNotNull(expected);
		assertEquals(InterruptedException.class, expected.getCause().getClass());
	}

	/**
	 * 
	 */
	@Test
	public void verifyInformObserver_FailureCase() throws Exception {
		chksum.addUpdateObserver(observer);
		final IOException expected = new IOException("expected");
		doThrow(expected).when(strategy).update(0, MILLISECONDS);

		ChecksumException expectedChksumException = null;
		try {
			chksum.update().getValue();
			fail("Exception expected here");
		} catch (final ChecksumException e) {
			expectedChksumException = e;
			assertSame(expected, e.getCause());
		}
		verify(observer, timeout(500)).failure(chksum, expectedChksumException);
	}

	/**
	 * 
	 */
	@Test
	public void verifyDoNotInformAboutError_ThrowNewError() throws Exception {
		final Error expected = new Error("expected");
		doThrow(expected).when(strategy).update(0, MILLISECONDS);
		chksum.update();
		sleep(500);
		verify(observer, never()).failure(Mockito.same(chksum), Mockito.any());
	}

	@Test
	public void verifyInformObserver_SuccessCase() throws Exception {
		chksum.addUpdateObserver(observer);
		when(strategy.digest()).thenReturn(RESULT_1);
		chksum.update();
		verify(observer, timeout(500)).success(chksum);
	}

	@Test
	public void verifyInformObserver_CancelledCase() throws Exception {
		chksum.addUpdateObserver(observer);
		when(strategy.digest()).thenReturn(null);
		doAnswer(new WaitFor(500)).when(strategy).update(0, MILLISECONDS);
		chksum.update();
		chksum.cancel();
		verify(strategy).cancel();
		verify(observer, timeout(2000)).cancel(chksum);
	}

	@Test
	public void verifyRemoveUpdateObserver() throws Exception {
		chksum.addUpdateObserver(observer);
		chksum.removeUpdateObserver(observer);
		chksum.update();
		sleep(500);
		verify(observer, never()).success(Mockito.any());
	}
}
