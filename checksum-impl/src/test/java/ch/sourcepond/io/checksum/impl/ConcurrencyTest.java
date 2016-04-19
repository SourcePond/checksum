package ch.sourcepond.io.checksum.impl;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.ChecksumException;

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
	private final UpdateStrategy strategy = mock(UpdateStrategy.class);
	private final ExecutorService executor = newCachedThreadPool();
	private final Checksum chksum = new DefaultChecksum(strategy, executor, executor);

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

	public void verifyInformObserver_FailureCase() {

	}

	public void verifyDoNotInformAboutError_ThrowNewError() {

	}

	public void verifyInformObserver_SuccessCase() {

	}

	public void verifyInformObserver_CancelledCase() {

	}
}
