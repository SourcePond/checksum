package ch.sourcepond.utils.checksum.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import ch.sourcepond.utils.checksum.Checksum;
import ch.sourcepond.utils.checksum.ChecksumFactory;
import ch.sourcepond.utils.checksum.PathChecksum;

/**
 * @author rolandhauser
 *
 */
public class DefaultChecksumFactoryTest {
	private static final String EXPECTED_HASH = "ff4ef4245da5b09786e3d3de8b430292fa081984db272d2b13ed404b45353d28";
	private static final String ALGORITHM = "SHA-256";
	private static final String CONTENT_FILE_NAME = "content.txt";
	private static final Path TEST_FILE = FileSystems.getDefault().getPath(SystemUtils.USER_DIR, "src", "test",
			"resources", "content.txt");
	private final ChecksumFactory factory = new DefaultChecksumFactory();

	/**
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void verifyCreateChecksumFromStream() throws NoSuchAlgorithmException, IOException, InterruptedException {
		final Checksum chsm = factory.create(getClass().getResourceAsStream("/" + CONTENT_FILE_NAME), ALGORITHM);
		assertEquals(EXPECTED_HASH, chsm.getHexValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void verifyCreatePathChecksum() throws NoSuchAlgorithmException, IOException, InterruptedException {
		final PathChecksum chsm = factory.create(TEST_FILE, ALGORITHM);
		assertEquals(EXPECTED_HASH, chsm.getHexValue());
	}
}
