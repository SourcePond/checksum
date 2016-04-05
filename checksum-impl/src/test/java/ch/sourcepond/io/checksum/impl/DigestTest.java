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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import ch.sourcepond.io.checksum.impl.Digest;

/**
 * @author rolandhauser
 *
 */
public class DigestTest {
	@SuppressWarnings("unchecked")
	private final Digest<Object> digest = mock(Digest.class);

	/**
	 * 
	 */
	@Test
	public void verifyCancel() {
		assertFalse(digest.isCancelled());
		digest.cancel();
		assertTrue(digest.isCancelled());
		digest.setCancelled(false);
		assertFalse(digest.isCancelled());
	}
}
