package ch.sourcepond.io.checksum;

import static ch.sourcepond.io.checksum.api.ChecksumFactory.SHA256;
import static ch.sourcepond.testing.OptionsHelper.defaultOptions;
import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.ChecksumFactory;
import ch.sourcepond.io.checksum.api.StreamSource;

/**
 * @author rolandhauser
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ChecksumFactoryTest {
	private static final String FIRST_EXPECTED_HASH = "40ab41c711d6979c8bfb9dae2022d79e4fa43b79bf5c74cc8d291936586a4778";
	private static final String SECOND_EXPECTED_HASH = "da821a59243b5f99e0d14c1e93b9e00b8e9632b60a07cc8168b78128773dfa31";
	private static final String TEST_CONTENT_FILE_NAME = "content.txt";
	private static final String FIRST_CONTENT_FILE_NAME = "first_content.txt";
	private static final String SECOND_CONTENT_FILE_NAME = "second_content.txt";
	private static final Path TEST_FILE = getDefault().getPath(SystemUtils.USER_DIR, "target", "test-classes",
			TEST_CONTENT_FILE_NAME);

	/**
	 * 
	 */
	@Inject
	private ChecksumFactory factory;

	/**
	 * @return
	 * @throws Exception
	 */
	@Configuration
	public Option[] config() throws Exception {
		return options(defaultOptions(getClass().getPackage().getName()),
				mavenBundle("commons-codec", "commons-codec").versionAsInProject());
	}

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
		final Checksum chsm = factory.create(SHA256, new StreamSource() {

			@Override
			public InputStream openStream() throws IOException {
				return asStream(FIRST_CONTENT_FILE_NAME);
			}
		});
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
		final Checksum chsm = factory.create(SHA256, TEST_FILE);
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
		final Checksum chsm = factory.create(SHA256, TEST_FILE.toUri().toURL());
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
		final Checksum chsm = factory.create(SHA256, resolveResourcesDirectory());
		assertEquals("dd3e119c99983d19b13fd51020f0f2562cde3788e5d36b7666b961bb159f16c8", chsm.getHexValue());
	}
}
