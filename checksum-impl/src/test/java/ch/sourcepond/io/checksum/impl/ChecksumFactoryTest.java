package ch.sourcepond.io.checksum.impl;

import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.io.checksum.Checksum;
import ch.sourcepond.io.checksum.ChecksumBuilder;
import ch.sourcepond.io.checksum.ChecksumBuilderFactory;
import ch.sourcepond.io.checksum.UpdatableChecksum;

/**
 * @author rolandhauser
 *
 */
public abstract class ChecksumFactoryTest {
	public static final String FIRST_EXPECTED_HASH = "40ab41c711d6979c8bfb9dae2022d79e4fa43b79bf5c74cc8d291936586a4778";
	private static final String SECOND_EXPECTED_HASH = "da821a59243b5f99e0d14c1e93b9e00b8e9632b60a07cc8168b78128773dfa31";
	public static final String ALGORITHM = "SHA-256";
	private static final String TEST_CONTENT_FILE_NAME = "content.txt";
	public static final String FIRST_CONTENT_FILE_NAME = "first_content.txt";
	private static final String SECOND_CONTENT_FILE_NAME = "second_content.txt";
	private static final Path TEST_FILE = getDefault().getPath(SystemUtils.USER_DIR, "target", "test-classes",
			TEST_CONTENT_FILE_NAME);
	private final ExecutorService executor = newFixedThreadPool(1);
	protected ChecksumBuilderFactory factory;
	protected ChecksumBuilder builder;

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		factory = getBuilderFactory();
		builder = factory.create(executor, ALGORITHM);
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		executor.shutdown();
	}

	/**
	 * @return
	 */
	protected abstract ChecksumBuilderFactory getBuilderFactory();

	/**
	 * @param pTestContentFileName
	 * @return
	 * @throws IOException
	 */
	private InputStream asStream(final String pTestContentFileName) throws IOException {
		return newInputStream(resolveResourcesDirectory().resolve(pTestContentFileName));
	}

	/**
	 * 
	 */
	private void copyContent(final String pTestContentFileName) throws IOException {
		try (final InputStream in = asStream(pTestContentFileName)) {
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
		final Checksum chsm = builder.create(asStream(FIRST_CONTENT_FILE_NAME));
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
		final UpdatableChecksum<Path> chsm = builder.create(TEST_FILE);
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
	public void verifyCreateUrlChecksum() throws Exception {
		copyContent(FIRST_CONTENT_FILE_NAME);
		final UpdatableChecksum<URL> chsm = builder.create(TEST_FILE.toUri().toURL());
		assertEquals(EMPTY, chsm.getPreviousHexValue());
		assertEquals(FIRST_EXPECTED_HASH, chsm.getHexValue());

		copyContent(SECOND_CONTENT_FILE_NAME);
		chsm.update();
		assertEquals(FIRST_EXPECTED_HASH, chsm.getPreviousHexValue());
		assertEquals(SECOND_EXPECTED_HASH, chsm.getHexValue());
	}

	/**
	 * @return
	 */
	private Path resolveResourcesDirectory() {
		return getResourceRootPath().resolve("src").resolve("test").resolve("resources");
	}

	/**
	 * @param pFs
	 * @return
	 */
	protected Path getResourceRootPath() {
		return getDefault().getPath(USER_DIR);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyDirectoryChecksum() throws Exception {
		final UpdatableChecksum<Path> chsm = builder.create(resolveResourcesDirectory());
		assertEquals("dd3e119c99983d19b13fd51020f0f2562cde3788e5d36b7666b961bb159f16c8", chsm.getHexValue());
	}
}
