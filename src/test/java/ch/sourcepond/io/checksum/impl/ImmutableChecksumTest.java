package ch.sourcepond.io.checksum.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.Future;

import org.junit.Test;

/**
 * @author rolandhauser
 *
 */
public class ImmutableChecksumTest extends BaseChecksumTest<OneTimeChecksum> {

	@SuppressWarnings("unchecked")
	@Override
	protected OneTimeChecksum createChecksum() throws Exception {
		final Future<byte[]> future = mock(Future.class);
		when(future.get()).thenReturn(VALUE);
		return new OneTimeChecksum(future, ANY_ALGORITHM);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyGetValueCopy() throws Exception {
		final byte[] value = checksum.getValue();
		assertNotSame(VALUE, value);
		assertArrayEquals(VALUE, value);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyGetHexValue() throws Exception {
		assertEquals(HEX_VALUE, checksum.getHexValue());
	}

}
