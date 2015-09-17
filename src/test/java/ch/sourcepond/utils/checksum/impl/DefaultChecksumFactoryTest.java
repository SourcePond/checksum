package ch.sourcepond.utils.checksum.impl;

import java.io.InputStream;

import org.junit.Test;

import ch.sourcepond.utils.checksum.ChecksumFactory;

/**
 * @author rolandhauser
 *
 */
public class DefaultChecksumFactoryTest extends ChecksumFactoryTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.utils.checksum.impl.ChecksumFactoryTest#getFactory()
	 */
	@Override
	protected ChecksumFactory getFactory() {
		return new DefaultChecksumFactory();
	}

	/**
	 * 
	 */
	@Test(expected = NullPointerException.class)
	public void verifyCreateNullInputStream() throws Exception {
		factory.create((InputStream) null, ALGORITHM);
	}

}
