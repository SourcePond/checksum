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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author rolandhauser
 *
 * @param <T>
 */
public abstract class UpdatableDigest<T> extends Digest<T>implements Cancellable {
	// To safe as much system resources as possible, we do not hold hard
	// references to the digester.
	private WeakReference<MessageDigest> digestRef;

	UpdatableDigest(final String pAlgorithm, final T pSource) {
		super(pAlgorithm, pSource);
		try {
			digestRef = new WeakReference<MessageDigest>(getInstance(pAlgorithm));
		} catch (final NoSuchAlgorithmException e) {
			// This can never happen because it has already been validated
			// during construction of the build that the algorithm is available.
			throw new InstantiationError(e.getMessage());
		}
	}

	protected final MessageDigest getDigest() {
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

	protected abstract byte[] doUpdateDigest() throws IOException;

	public final byte[] updateDigest() throws IOException {
		try {
			return doUpdateDigest();
		} finally {
			setCancelled(false);
		}
	}
}
