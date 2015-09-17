package ch.sourcepond.utils.checksum.impl;

import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.Files.copy;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.utils.checksum.Checksum;
import ch.sourcepond.utils.checksum.ChecksumFactory;
import ch.sourcepond.utils.checksum.PathChecksum;

/**
 * @author rolandhauser
 *
 */
public abstract class ChecksumFactoryTest {
	static final String FIRST_EXPECTED_HASH = "40ab41c711d6979c8bfb9dae2022d79e4fa43b79bf5c74cc8d291936586a4778";
	private static final String SECOND_EXPECTED_HASH = "da821a59243b5f99e0d14c1e93b9e00b8e9632b60a07cc8168b78128773dfa31";
	static final String ALGORITHM = "SHA-256";
	private static final String TEST_CONTENT_FILE_NAME = "content.txt";
	static final String FIRST_CONTENT_FILE_NAME = "first_content.txt";
	private static final String SECOND_CONTENT_FILE_NAME = "second_content.txt";
	private static final Path TEST_FILE = getDefault().getPath(SystemUtils.USER_DIR, "target", "test-classes",
			TEST_CONTENT_FILE_NAME);
	protected ChecksumFactory factory;

	/**
	 * 
	 */
	@Before
	public void setup() {
		factory = getFactory();
	}

	/**
	 * @return
	 */
	protected abstract ChecksumFactory getFactory();

	/**
	 * 
	 */
	private void copyContent(final String pTestContentFileName) throws IOException {
		try (final InputStream in = getClass().getResourceAsStream("/" + pTestContentFileName)) {
			copy(in, TEST_FILE, REPLACE_EXISTING);
		}
	}

	/**
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void verifyCreateChecksumFromStream() throws Exception {
		copyContent(FIRST_CONTENT_FILE_NAME);
		final Checksum chsm = factory.create(getClass().getResourceAsStream("/" + FIRST_CONTENT_FILE_NAME), ALGORITHM);
		assertEquals(FIRST_EXPECTED_HASH, chsm.getHexValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void verifyCreateFileChecksum() throws Exception {
		copyContent(FIRST_CONTENT_FILE_NAME);
		final PathChecksum chsm = factory.create(TEST_FILE, ALGORITHM);
		assertEquals(EMPTY, chsm.getPreviousHexValue());
		assertEquals(FIRST_EXPECTED_HASH, chsm.getHexValue());

		copyContent(SECOND_CONTENT_FILE_NAME);
		chsm.update();
		assertEquals(FIRST_EXPECTED_HASH, chsm.getPreviousHexValue());
		assertEquals(SECOND_EXPECTED_HASH, chsm.getHexValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void verifyCreateFileChecksumWithExecutor() throws Exception {
		final ExecutorService executor = Executors.newFixedThreadPool(1);
		try {
			copyContent(FIRST_CONTENT_FILE_NAME);
			final PathChecksum chsm = factory.create(TEST_FILE, ALGORITHM, executor);
			assertEquals(EMPTY, chsm.getPreviousHexValue());
			assertEquals(FIRST_EXPECTED_HASH, chsm.getHexValue());

			copyContent(SECOND_CONTENT_FILE_NAME);
			chsm.update();
			assertEquals(FIRST_EXPECTED_HASH, chsm.getPreviousHexValue());
			assertEquals(SECOND_EXPECTED_HASH, chsm.getHexValue());
		} finally {
			executor.shutdown();
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyDirectoryChecksum() throws Exception {
		final PathChecksum chsm = factory.create(getDefault().getPath(USER_DIR, "src", "test", "resources"), ALGORITHM);
		assertEquals("dd3e119c99983d19b13fd51020f0f2562cde3788e5d36b7666b961bb159f16c8", chsm.getHexValue());
	}
}
