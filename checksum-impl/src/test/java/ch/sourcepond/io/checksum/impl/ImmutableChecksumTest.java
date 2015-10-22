package ch.sourcepond.io.checksum.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.concurrent.Future;

import org.junit.Test;

import ch.sourcepond.io.checksum.impl.digest.Digest;

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
		final Digest<InputStream> digest = mock(Digest.class);
		when(digest.getAlgorithm()).thenReturn(ANY_ALGORITHM);
		return new OneTimeChecksum(digest, future);
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
