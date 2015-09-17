package ch.sourcepond.io.checksum.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import ch.sourcepond.io.checksum.impl.ImmutableChecksum;

/**
 * @author rolandhauser
 *
 */
public class ImmutableChecksumTest extends BaseChecksumTest<ImmutableChecksum> {

	@Override
	protected ImmutableChecksum createChecksum() {
		return new ImmutableChecksum(VALUE, ANY_ALGORITHM);
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
