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

import static org.slf4j.LoggerFactory.getLogger;

import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;

import ch.sourcepond.io.checksum.api.Algorithm;
import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.ChecksumFactory;
import ch.sourcepond.io.checksum.api.StreamSource;

/**
 * Default implementation of the {@link ChecksumFactory} interface. An instance
 * of this class will be exported as OSGi-service via Blueprint.
 *
 */
public final class DefaultChecksumFactory implements ChecksumFactory {
	private static final Logger LOG = getLogger(DefaultChecksumFactory.class);
	private final ExecutorService updateExecutor;
	private final ExecutorService listenerExecutor;
	private final UpdateStrategyFactory updateStrategyFactory;

	/**
	 * @param pUpdateExecutor
	 */
	public DefaultChecksumFactory(final ExecutorService pUpdateExecutor, final ExecutorService pListenerExecutor,
			final UpdateStrategyFactory pDigestFactory) {
		updateExecutor = pUpdateExecutor;
		listenerExecutor = pListenerExecutor;
		updateStrategyFactory = pDigestFactory;
	}

	@Override
	public Checksum create(final String pAlgorithm, final StreamSource pSource) throws NoSuchAlgorithmException {
		return new DefaultChecksum(updateStrategyFactory.newStrategy(pAlgorithm, pSource), updateExecutor,
				listenerExecutor);
	}

	@Override
	public Checksum create(final String pAlgorithm, final Path pPath) throws NoSuchAlgorithmException {
		return new DefaultChecksum(updateStrategyFactory.newStrategy(pAlgorithm, pPath), updateExecutor,
				listenerExecutor);
	}

	@Override
	public Checksum create(final String pAlgorithm, final URL pUrl) throws NoSuchAlgorithmException {
		return new DefaultChecksum(updateStrategyFactory.newStrategy(pAlgorithm, new UrlStreamSource(pUrl)),
				updateExecutor, listenerExecutor);
	}

	@Override
	public Checksum create(final Algorithm pAlgorithm, final StreamSource pSource) {
		try {
			return create(pAlgorithm.toString(), pSource);
		} catch (final NoSuchAlgorithmException e) {
			// This should never happen
			LOG.error(e.getMessage(), e);
			throw new InstantiationError(e.getMessage());
		}
	}

	@Override
	public Checksum create(final Algorithm pAlgorithm, final Path pPath) {
		try {
			return create(pAlgorithm.toString(), pPath);
		} catch (final NoSuchAlgorithmException e) {
			// This should never happen
			LOG.error(e.getMessage(), e);
			throw new InstantiationError(e.getMessage());
		}
	}

	@Override
	public Checksum create(final Algorithm pAlgorithm, final URL pUrl) {
		try {
			return create(pAlgorithm.toString(), pUrl);
		} catch (final NoSuchAlgorithmException e) {
			// This should never happen
			LOG.error(e.getMessage(), e);
			throw new InstantiationError(e.getMessage());
		}
	}
}
