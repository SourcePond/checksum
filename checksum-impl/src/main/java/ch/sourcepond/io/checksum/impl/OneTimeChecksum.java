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

import java.io.InputStream;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import ch.sourcepond.io.checksum.ChecksumBuilder;
import ch.sourcepond.io.checksum.ChecksumException;
import ch.sourcepond.io.checksum.impl.digest.Digest;

/**
 * Default implementation for a one-time checksum created through
 * {@link ChecksumBuilder#create(java.io.InputStream)}.
 */
final class OneTimeChecksum extends BaseChecksum {
	private final Digest<InputStream> digest;
	private final Future<byte[]> future;

	/**
	 * @param pDigest
	 * @param pFuture
	 */
	OneTimeChecksum(final Digest<InputStream> pDigest, final Future<byte[]> pFuture) {
		digest = pDigest;
		future = pFuture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.Checksum#getAlgorithm()
	 */
	@Override
	public String getAlgorithm() {
		return digest.getAlgorithm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.BaseChecksum#getValueUnsynchronized()
	 */
	@Override
	protected byte[] evaluateValue() throws ChecksumException {
		try {
			byte[] rc = future.get();
			if (rc == null) {
				rc = INITIAL;
			}
			return rc;
		} catch (CancellationException | InterruptedException | ExecutionException e) {
			throw new ChecksumException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.Checksum#cancel()
	 */
	@Override
	public void cancel() {
		digest.cancel();
	}
}
