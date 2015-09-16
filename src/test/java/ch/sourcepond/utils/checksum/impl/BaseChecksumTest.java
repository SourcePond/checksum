package ch.sourcepond.utils.checksum.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

/**
 * @author rolandhauser
 *
 * @param <T>
 */
public abstract class BaseChecksumTest<T extends BaseChecksum> {
	public static final String ANY_ALGORITHM = "anyAlgorith";
	public static final String HEX_VALUE = "01030305";
	protected static final byte[] VALUE = new byte[] { 1, 3, 3, 5 };
	protected T checksum;

	/**
	 * @return
	 */
	protected abstract T createChecksum();

	/**
	 * 
	 */
	@Before
	public void setup() {
		checksum = createChecksum();
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
	public void verifyCopyArray() {
		final byte[] copy = checksum.copyArray(VALUE);
		assertNotSame(VALUE, copy);
		assertArrayEquals(VALUE, copy);
	}
}
