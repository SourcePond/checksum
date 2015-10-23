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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.concurrent.Future;

import org.junit.Test;

import ch.sourcepond.io.checksum.impl.digest.Digest;

/**
 * @author rolandhauser
 *
 */
public class ImmutableChecksumTest extends BaseChecksumTest<OneTimeChecksum> {

	@SuppressWarnings("unchecked")
	@Override
	protected OneTimeChecksum createChecksum() throws Exception {
		final Future<byte[]> future = mock(Future.class);
		when(future.get()).thenReturn(VALUE);
		final Digest<InputStream> digest = mock(Digest.class);
		when(digest.getAlgorithm()).thenReturn(ANY_ALGORITHM);
		return new OneTimeChecksum(digest, future);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyGetValueCopy() throws Exception {
		final byte[] value = checksum.getValue();
		assertNotSame(VALUE, value);
		assertArrayEquals(VALUE, value);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyGetHexValue() throws Exception {
		assertEquals(HEX_VALUE, checksum.getHexValue());
	}

}
