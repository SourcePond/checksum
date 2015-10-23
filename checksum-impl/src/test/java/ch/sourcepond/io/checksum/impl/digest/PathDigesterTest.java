/*Copyright (C) 2015 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
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
