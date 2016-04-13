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
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 */
abstract class BaseUpdateStrategy<T> implements UpdateStrategy {
	private final String algorithm;
	private final T source;
	private volatile boolean cancelled;
	// To safe as much system resources as possible, we do not hold hard
	// references to the digester.
	private WeakReference<MessageDigest> digestRef;
	// Temporary digest; this should only be initialized for consecutive
	// sequence of update requests i.e. from a starting update until digest
	private MessageDigest tmpDigest;

	BaseUpdateStrategy(final String pAlgorithm, final T pSource) throws NoSuchAlgorithmException {
		algorithm = pAlgorithm;
		source = pSource;
		digestRef = new WeakReference<MessageDigest>(getInstance(pAlgorithm));
	}

	protected final MessageDigest getTmpDigest() {
		return tmpDigest;
	}

	private MessageDigest getWeakDigest() {
		// Initialize the temporary hard reference to the digester; this must be
		// set to null after the update has been performed.
		MessageDigest tempDigest = digestRef.get();
		if (tempDigest == null) {
			try {
				tempDigest = getInstance(getAlgorithm());
			} catch (final NoSuchAlgorithmException e) {
				// This can never happen because it has already been validated
				// during construction that the algorithm is available.
			}
			digestRef = new WeakReference<MessageDigest>(tempDigest);
		}
		return tempDigest;
	}

	protected abstract void doUpdate() throws IOException;

	@Override
	public final void update() throws IOException {
		try {
			// Initialize the temporary hard reference to the digester; this
			// must be
			// set to null after the update has been performed.
			if (tmpDigest == null) {
				tmpDigest = getWeakDigest();
			}
			doUpdate();
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
			notNull(tmpDigest, "tmpDigest digest is null");
			try {
				digest = tmpDigest.digest();
			} finally {
				// This is important; we need to clear the hard-reference to the
				// digest. Otherwise it would not make any sense to hold a
				// WeakReference ;-)
				tmpDigest = null;
			}
		}
		return digest;
	}

	public T getSource() {
		return source;
	}

	@Override
	public final boolean isCancelled() {
		return cancelled;
	}

	@Override
	public final void cancel() {
		if (!cancelled) {
			cancelled = true;
		}
		// Important: clear the hard-reference to the digest object (if not
		// already done).
		if (tmpDigest != null) {
			tmpDigest = null;
		}
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
