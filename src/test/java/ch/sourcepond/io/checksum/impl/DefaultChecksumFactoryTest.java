package ch.sourcepond.io.checksum.impl;

import java.io.InputStream;
import java.nio.file.Path;

import org.junit.Test;

import ch.sourcepond.io.checksum.ChecksumBuilderFactory;
import ch.sourcepond.io.checksum.impl.digest.DefaultDigestFactory;

/**
 * @author rolandhauser
 *
 */
public class DefaultChecksumFactoryTest extends ChecksumFactoryTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.ChecksumFactoryTest#getFactory()
	 */
	@Override
	protected ChecksumBuilderFactory getBuilderFactory() {
		return new DefaultChecksumBuilderFactory(new DefaultDigestFactory());
	}

	/**
	 * 
	 */
	@Test(expected = NullPointerException.class)
	public void verifyCreateOneTimeNullInputStream() throws Exception {
		builder.create((InputStream) null);
	}

	/**
	 * 
	 */
	@Test(expected = NullPointerException.class)
	public void verifyCreateNullPath() throws Exception {
		builder.create((Path) null);
	}
}
