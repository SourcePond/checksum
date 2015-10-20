package ch.sourcepond.io.checksum.impl.digest;

import static ch.sourcepond.io.checksum.impl.ChecksumFactoryTest.FIRST_CONTENT_FILE_NAME;
import static ch.sourcepond.io.checksum.impl.ChecksumFactoryTest.FIRST_EXPECTED_HASH;
import static java.nio.file.FileSystems.getDefault;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.junit.Assert.assertEquals;

import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

/**
 * @author rolandhauser
 *
 */
public class PathDigesterTest {
	private static final String ALGORITHM = "SHA-256";
	private final Path file = getDefault().getPath(USER_DIR, "src", "test", "resources", FIRST_CONTENT_FILE_NAME);
	private PathDigest digester;

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		digester = new PathDigest(ALGORITHM, file);
	}

	/**
	 * 
	 */
	@Test
	public void verifyUpdateDigest() throws Exception {
		final byte[] result = digester.updateDigest();
		assertEquals(FIRST_EXPECTED_HASH, encodeHexString(result));
	}

	/**
	 * 
	 */
	@Test
	public void verifyUpdateDigestGCRun() throws Exception {
		// After running the garbage collector the weak-references should have
		// been cleared.
		Runtime.getRuntime().gc();

		// This should not cause an exception; weak-references are initialized
		// with new values.
		final byte[] result = digester.updateDigest();
		assertEquals(FIRST_EXPECTED_HASH, encodeHexString(result));
	}
}
