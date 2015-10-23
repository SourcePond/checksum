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

import static ch.sourcepond.io.checksum.impl.BaseChecksum.INITIAL;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;

import ch.sourcepond.io.checksum.ChecksumException;
import ch.sourcepond.io.checksum.impl.digest.Digest;

/**
 * @author rolandhauser
 *
 */
public class OneTimeChecksumTest {
	@SuppressWarnings("unchecked")
	private final Future<byte[]> future = mock(Future.class);
	@SuppressWarnings("unchecked")
	private final Digest<InputStream> digest = mock(Digest.class);
	private final OneTimeChecksum checksum = new OneTimeChecksum(digest, future);

	/**
	 * 
	 */
	@Test
	public void verifyEvaluateValueReturnsNull() throws Exception {
		assertSame(INITIAL, checksum.evaluateValue());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void veriyEvaluateValueThrowsException() throws Exception {
		final ExecutionException expected = new ExecutionException(new Exception());
		doThrow(expected).when(future).get();
		try {
			checksum.evaluateValue();
			fail("Exception failed");
		} catch (ChecksumException ex) {
			assertSame(expected, ex.getCause());
		}
	}

	/**
	 * 
	 */
	@Test
	public void verifyCancel() {
		checksum.cancel();
		assertTrue(digest.isCancelled());
	}
}
