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

import static ch.sourcepond.io.checksum.impl.digest.DigestHelper.perform;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
final class UrlDigest extends UpdatableDigest<URL> {

	/**
	 * @param pAlgorithm
	 * @param pSource
	 * @throws NoSuchAlgorithmException
	 */
	UrlDigest(final String pAlgorithm, final URL pSource) {
		super(pAlgorithm, pSource);
	}

	/**
	 * @return
	 * @throws IOException
	 */
	@Override
	public byte[] updateDigest() throws IOException {
		try {
			return perform(getDigest(), this, getSource().openStream());
		} finally {
			setCancelled(false);
		}
	}
}
