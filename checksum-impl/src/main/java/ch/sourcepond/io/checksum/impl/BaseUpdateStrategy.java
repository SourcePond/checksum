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

import static java.lang.Thread.currentThread;
import static java.security.MessageDigest.getInstance;
import static org.apache.commons.lang3.Validate.notNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;

/**
 * 
 */
abstract class BaseUpdateStrategy<T> implements UpdateStrategy {
	private static final Logger LOG = getLogger(BaseUpdateStrategy.class);
	/**
	 * 
	 */
	static final int DEFAULT_BUFFER_SIZE = 8192;
	static final int EOF = -1;
	private final String algorithm;
	private final T source;
	private boolean cancelled;
	private final MessageDigest digest;

	BaseUpdateStrategy(final String pAlgorithm, final T pSource) throws NoSuchAlgorithmException {
		notNull(pAlgorithm, "Algorithm is null");
		notNull(pSource, "Source is null");

		algorithm = pAlgorithm;
		source = pSource;
		digest = getInstance(algorithm);
	}

	protected MessageDigest getDigest() {
		return digest;
	}

	/**
	 * @param pInterval
	 * @param pUnit
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@GuardedBy("this")
	protected final void wait(final long pInterval, final TimeUnit pUnit) throws IOException {
		if (isCancelled()) {
			LOG.debug("Checksum calculation cancelled by user.");
		} else if (pInterval > 0) {
			try {
				wait(pUnit.toMillis(pInterval));
			} catch (final InterruptedException e) {
				currentThread().interrupt();
				throw new IOException(e.getMessage(), e);
			}
		}
	}

	protected abstract void doUpdate(long pInterval, TimeUnit pUnit) throws IOException;

	@GuardedBy(value = "this")
	@Override
	public final void update(final long pInterval, final TimeUnit pUnit) throws IOException {
		try {
			doUpdate(pInterval, pUnit);
		} finally {
			if (cancelled) {
				cancelled = false;
			}
		}
	}

	@GuardedBy(value = "this")
	@Override
	public final byte[] digest() {
		byte[] digest = null;
		if (!cancelled) {
			digest = getDigest().digest();
		}
		return digest;
	}

	public final T getSource() {
		return source;
	}

	/**
	 * Checks, whether a user has requested to cancel to current update.
	 * 
	 * @return {@code true} if cancelled, {@code false} otherwise.
	 */
	@GuardedBy("this")
	protected final boolean isCancelled() {
		return cancelled;
	}

	@GuardedBy("this")
	@Override
	public final void cancel() {
		if (!cancelled) {
			cancelled = true;
			getDigest().reset();
		}
	}

	@Override
	public final String getAlgorithm() {
		return algorithm;
	}

	@GuardedBy("this")
	@Override
	protected synchronized final void finalize() {
		cancel();
	}
}
