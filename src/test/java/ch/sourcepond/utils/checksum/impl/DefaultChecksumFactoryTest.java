package ch.sourcepond.utils.checksum.impl;

import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.nio.file.Path;

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

	/**
	 * 
	 */
	@Test(expected = NullPointerException.class)
	public void verifyCreateNullAlgorithm() throws Exception {
		factory.create(mock(InputStream.class), null);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void verifyCreateBlankAlgorithm() throws Exception {
		factory.create(mock(InputStream.class), "  ");
	}

	/**
	 * 
	 */
	@Test(expected = NullPointerException.class)
	public void verifyCreateNullPath() throws Exception {
		factory.create((Path) null, ALGORITHM);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void verifyCreateWithPathBlankAlgorithm() throws Exception {
		factory.create(mock(Path.class), "  ");
	}

	/**
	 * 
	 */
	@Test(expected = NullPointerException.class)
	public void verifyCreateNullExecutor() throws Exception {
		factory.create(mock(Path.class), ALGORITHM, null);
	}

}
