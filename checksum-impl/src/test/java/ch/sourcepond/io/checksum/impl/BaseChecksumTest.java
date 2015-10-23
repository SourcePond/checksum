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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

/**
 * @author rolandhauser
 *
 * @param <T>
 */
public abstract class BaseChecksumTest<T extends BaseChecksum> {
	public static final String ANY_ALGORITHM = "anyAlgorith";
	public static final String HEX_VALUE = "01030305";
	protected static final byte[] VALUE = new byte[] { 1, 3, 3, 5 };
	protected T checksum;

	/**
	 * @return
	 */
	protected abstract T createChecksum() throws Exception;

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		checksum = createChecksum();
	}

	/**
	 * 
	 */
	@Test
	public void verifyGetAlgorithm() {
		assertEquals(ANY_ALGORITHM, checksum.getAlgorithm());
	}

	/**
	 * 
	 */
	@Test
	public void verifyCopyArray() {
		final byte[] copy = checksum.copyArray(VALUE);
		assertNotSame(VALUE, copy);
		assertArrayEquals(VALUE, copy);
	}
}
