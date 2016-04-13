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

import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import ch.sourcepond.io.checksum.api.StreamSource;

/**
 * Default implementation of the {@link UpdateStrategyFactory} interface.
 */
public class UpdateStrategyFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.io.checksum.impl.digest.DigestFactory#newPathDigest(java.
	 * lang.String, java.nio.file.Path)
	 */
	public UpdateStrategy newStrategy(final String pAlgorithm, final Path pPath) throws NoSuchAlgorithmException {
		return new PathUpdateStrategy(pAlgorithm, pPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.io.checksum.impl.digest.DigestFactory#newUrlDigest(java.
	 * lang.String, java.net.URL)
	 */
	public UpdateStrategy newStrategy(final String pAlgorithm, final StreamSource pSource)
			throws NoSuchAlgorithmException {
		return new StreamSourceUpdateStrategy(pAlgorithm, pSource);
	}
}
