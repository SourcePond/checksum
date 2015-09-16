package ch.sourcepond.utils.checksum.impl;

import static ch.sourcepond.utils.checksum.impl.DefaultPathChecksum.INITIAL;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.utils.checksum.ChecksumException;

/**
 * @author rolandhauser
 *
 */
public class DefaultPathChecksumTest extends BaseChecksumTest<DefaultPathChecksum> {

	/**
	 * @author rolandhauser
	 *
	 */
	private class InterruptTest implements Runnable {
		private boolean fail;
		private ChecksumException exception;

		@Override
		public void run() {
			try {
				checksum.update();
				currentThread().interrupt();
				checksum.getValue();
				fail = true;
			} catch (final ChecksumException e) {
				exception = e;
			}
		}
	}

	private static final byte[] SECOND_VALUE = new byte[] { 98, 49, 53, 50 };
	private final Path path = mock(Path.class);
	private final PathDigester digester = mock(PathDigester.class);
	private final ScheduledExecutorService delegate = newScheduledThreadPool(1);
	private final Executor executor = new Executor() {

		@Override
		public void execute(final Runnable command) {
			// This should cause the checksum methods to wait until update is
			// done.
			delegate.schedule(checksum, 500, TimeUnit.MILLISECONDS);
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.utils.checksum.impl.BaseChecksumTest#setup()
	 */
	@Override
	@Before
	public void setup() {
		super.setup();
		when(digester.getAlgorithm()).thenReturn(ANY_ALGORITHM);
		when(digester.getPath()).thenReturn(path);
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
	public void verifyGetPath() {
		assertEquals(path, checksum.getPath());
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
		when(digester.updateDigest()).thenReturn(VALUE).thenReturn(SECOND_VALUE);
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
		when(digester.updateDigest()).thenReturn(VALUE).thenReturn(SECOND_VALUE);
		checksum.update();
		checksum.update();
		assertEquals(HEX_VALUE, checksum.getPreviousHexValue());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyEqualsPrevious() throws Exception {
		when(digester.updateDigest()).thenReturn(VALUE).thenReturn(SECOND_VALUE).thenReturn(VALUE).thenReturn(VALUE);
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
		when(digester.updateDigest()).thenReturn(VALUE);
		checksum.update();
		assertArrayEquals(VALUE, checksum.getValue());
	}

	/**
	 * 
	 */
	@Test
	public void verifyGetPreviousValue() throws Exception {
		when(digester.updateDigest()).thenReturn(VALUE).thenReturn(SECOND_VALUE);
		checksum.update();
		checksum.update();
		assertArrayEquals(VALUE, checksum.getPreviousValue());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyGetValueIOExceptionOccurred() throws Exception {
		final IOException expected = new IOException();
		doThrow(expected).when(digester).updateDigest();
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
		doThrow(expected).when(digester).updateDigest();
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
		doThrow(expected).when(digester).updateDigest();
		checksum.update();

		try {
			checksum.getValue();
			fail("Exception expected here");
		} catch (final Error e) {
			assertSame(expected, e.getCause());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.utils.checksum.impl.BaseChecksumTest#createChecksum()
	 */
	@Override
	protected DefaultPathChecksum createChecksum() {
		return new DefaultPathChecksum(digester, executor);
	}
}
