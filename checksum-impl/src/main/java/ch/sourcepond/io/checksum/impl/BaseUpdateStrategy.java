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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	private final Lock lock = new ReentrantLock();
	private final Condition waitCondition = lock.newCondition();
	private final String algorithm;
	private final T source;
	private volatile boolean cancelled;
	private final MessageDigest digest;

	BaseUpdateStrategy(final String pAlgorithm, final T pSource) throws NoSuchAlgorithmException {
		notNull(pAlgorithm, "Algorithm is null");
		notNull(pSource, "Source is null");

		algorithm = pAlgorithm;
		source = pSource;
		digest = getInstance(pAlgorithm);
	}

	protected final MessageDigest getDigest() {
		return digest;
	}

	/**
	 * @param pInterval
	 * @param pUnit
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected final void wait(final long pInterval, final TimeUnit pUnit) throws IOException {
		if (isCancelled()) {
			LOG.debug("Checksum calculation cancelled by user.");
		} else if (pInterval > 0) {
			lock.lock();
			try {
				waitCondition.await(pInterval, pUnit);
			} catch (final InterruptedException e) {
				currentThread().interrupt();
				throw new IOException(e.getMessage(), e);
			} finally {
				lock.unlock();
			}
		}
	}

	protected abstract void doUpdate(final long pInterval, final TimeUnit pUnit) throws IOException;

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

	@Override
	public final byte[] digest() {
		byte[] digest = null;
		if (!cancelled) {
			digest = getDigest().digest();
		}
		return digest;
	}

	public T getSource() {
		return source;
	}

	/**
	 * Checks, whether a user has requested to cancel to current update.
	 * 
	 * @return {@code true} if cancelled, {@code false} otherwise.
	 */
	protected final boolean isCancelled() {
		return cancelled;
	}

	@Override
	public final void cancel() {
		if (!cancelled) {
			cancelled = true;
		}
		getDigest().reset();
	}

	@Override
	public String getAlgorithm() {
		return algorithm;
	}

	@Override
	protected final void finalize() throws Throwable {
		cancel();
	}
}
