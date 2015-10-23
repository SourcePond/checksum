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

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link DigestFactory} interface.
 */
@Named // Necessary to make this component work with Eclipse Sisu
@Singleton
public class DefaultDigestFactory implements DigestFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.io.checksum.impl.digest.DigestFactory#newDigestTask(java.
	 * lang.String, java.io.InputStream)
	 */
	@Override
	public InputStreamDigest newDigestTask(final String pAlgorithm, final InputStream pSource) {
		return new InputStreamDigest(pAlgorithm, pSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.io.checksum.impl.digest.DigestFactory#newPathDigest(java.
	 * lang.String, java.nio.file.Path)
	 */
	@Override
	public UpdatableDigest<Path> newDigest(final String pAlgorithm, final Path pPath) {
		return new PathDigest(pAlgorithm, pPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.io.checksum.impl.digest.DigestFactory#newUrlDigest(java.
	 * lang.String, java.net.URL)
	 */
	@Override
	public UpdatableDigest<URL> newDigest(final String pAlgorithm, final URL pUrl) {
		return new UrlDigest(pAlgorithm, pUrl);
	}
}
