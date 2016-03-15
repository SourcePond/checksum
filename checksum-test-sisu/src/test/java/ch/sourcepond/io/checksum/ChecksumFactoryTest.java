package ch.sourcepond.io.checksum;

import javax.inject.Inject;

import ch.sourcepond.io.checksum.impl.ChecksumFactoryIntegrationTest;

/**
 * @author rolandhauser
 *
 */
public class ChecksumFactoryTest extends ChecksumFactoryIntegrationTest {

	/**
	 * 
	 */
	@Inject
	private ChecksumFactory factory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.ChecksumFactoryTest#getFactory()
	 */
	@Override
	protected ChecksumFactory getBuilderFactory() {
		return factory;
	}
}
