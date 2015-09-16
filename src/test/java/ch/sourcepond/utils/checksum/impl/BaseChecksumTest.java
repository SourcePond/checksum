package ch.sourcepond.utils.checksum.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

/**
 * @author rolandhauser
 *
 * @param <T>
 */
public abstract class BaseChecksumTest<T extends BaseChecksum> {
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
	public void verifyCopyArray() {
		final byte[] copy = checksum.copyArray(VALUE);
		assertNotSame(VALUE, copy);
		assertArrayEquals(VALUE, copy);
	}
}
