package ch.sourcepond.utils.checksum.impl;

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

}
