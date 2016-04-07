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
package ch.sourcepond.io.checksum.impl;

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
public class PathUpdateStrategyTest {
	public static final String EXPECTED_HASH = "40ab41c711d6979c8bfb9dae2022d79e4fa43b79bf5c74cc8d291936586a4778";
	private static final String ALGORITHM = "SHA-256";
	private final Path file = getDefault().getPath(USER_DIR, "src", "test", "resources", "first_content.txt");
	private PathUpdateStrategy strategy;

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		strategy = new PathUpdateStrategy(ALGORITHM, file);
	}

	/**
	 * 
	 */
	@Test
	public void verifyUpdateDigest() throws Exception {
		final byte[] result = strategy.update();
		assertEquals(EXPECTED_HASH, encodeHexString(result));
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
		final byte[] result = strategy.update();
		assertEquals(EXPECTED_HASH, encodeHexString(result));
	}
}