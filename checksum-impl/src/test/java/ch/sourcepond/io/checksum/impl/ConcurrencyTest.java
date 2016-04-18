package ch.sourcepond.io.checksum.impl;

import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.concurrent.ExecutorService;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.sourcepond.io.checksum.api.UpdateableChecksum;

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

	private final UpdateStrategy strategy = mock(UpdateStrategy.class);
	private final ExecutorService executor = newCachedThreadPool();
	private final UpdateableChecksum chksum = new DefaultChecksum(strategy, executor);

	/**
	 * 
	 */
	@Test
	public void verifyAllowExactlyOneWaitingUpdate() throws Exception {
		doAnswer(new WaitFor(1000)).when(strategy).update(1000, MILLISECONDS);
		doAnswer(new WaitFor(500)).when(strategy).update(500, MILLISECONDS);
		doThrow(AssertionError.class).when(strategy).update(0, MILLISECONDS);

		// Call update 3 times;
		chksum.update(1000).update(500).update(0).getValue();
	}

	public void verifyIllegalStateExceptionWhenUpdate_ObserverLockAlreadyHeld() {

	}

	public void verifyInterruptFlagWhenAwaitCalcuationInterrupted() {

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
