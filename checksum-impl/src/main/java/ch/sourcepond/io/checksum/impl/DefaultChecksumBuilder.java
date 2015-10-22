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

import static java.security.MessageDigest.getInstance;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;

import ch.sourcepond.io.checksum.Checksum;
import ch.sourcepond.io.checksum.ChecksumBuilder;
import ch.sourcepond.io.checksum.ChecksumException;
import ch.sourcepond.io.checksum.UpdatableChecksum;
import ch.sourcepond.io.checksum.impl.digest.DigestFactory;
import ch.sourcepond.io.checksum.impl.digest.InputStreamDigester;
import ch.sourcepond.io.checksum.impl.digest.UpdatableDigest;

/**
 * Default implementation of the {@link ChecksumBuilder} interface.
 * 
 */
final class DefaultChecksumBuilder implements ChecksumBuilder {
	private final ExecutorService defaultExecutor;
	private final DigestFactory digestFactory;
	private final String algorithm;

	/**
	 * @param pAlgorithm
	 * @throws NoSuchAlgorithmException
	 */
	DefaultChecksumBuilder(final DigestFactory pDigestFactory, final ExecutorService pDefaultExecutor,
			final String pAlgorithm) throws NoSuchAlgorithmException {
		// Verify, that the algorithm exists
		getInstance(pAlgorithm);
		digestFactory = pDigestFactory;
		defaultExecutor = pDefaultExecutor;
		algorithm = pAlgorithm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.io.checksum.ChecksumBuilder#create(java.io.InputStream,
	 * java.util.concurrent.ExecutorService)
	 */
	@Override
	public Checksum create(final InputStream pInputStream) throws IOException {
		notNull(pInputStream, "InputStream is null!");
		notBlank(algorithm, "Algorithm is null or blank!");
		final InputStreamDigester digest = digestFactory.newDigestTask(algorithm, pInputStream);
		return new OneTimeChecksum(digest, defaultExecutor.submit(digest));
	}

	/**
	 * @param pSource
	 * @param pDigest
	 * @param pExecutor
	 * @return
	 * @throws ChecksumException
	 */
	private <T> UpdatableChecksum<T> createChecksum(final T pSource, final UpdatableDigest<T> pDigest)
			throws ChecksumException {
		notNull(pSource, "Source is null!");
		final UpdatableChecksum<T> chsm = new DefaultUpdatableChecksum<>(pDigest, defaultExecutor);
		chsm.update();
		return chsm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.io.checksum.ChecksumBuilderFactory#create(java.nio.file.
	 * Path, java.lang.String, java.util.concurrent.Executor)
	 */
	@Override
	public UpdatableChecksum<Path> create(final Path pPath) throws ChecksumException {
		try {
			return createChecksum(pPath, digestFactory.newDigest(algorithm, pPath));
		} catch (final NoSuchAlgorithmException e) {
			// This should never happen because it has been verified that the
			// algorithm exists during construction of this builder.
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * @param pUrl
	 * @return
	 * @throws ChecksumException
	 */
	@Override
	public UpdatableChecksum<URL> create(final URL pUrl) throws ChecksumException {
		try {
			return createChecksum(pUrl, digestFactory.newDigest(algorithm, pUrl));
		} catch (final NoSuchAlgorithmException e) {
			// This should never happen because it has been verified that the
			// algorithm exists during construction of this builder.
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
