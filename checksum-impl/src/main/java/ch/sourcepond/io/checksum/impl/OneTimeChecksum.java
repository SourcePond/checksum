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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import ch.sourcepond.io.checksum.ChecksumException;
import ch.sourcepond.io.checksum.impl.digest.Digest;

/**
 *
 */
final class OneTimeChecksum extends BaseChecksum {
	private final Digest digest;
	private final Future<byte[]> future;

	/**
	 * @param pValue
	 * @param pAlgorithm
	 */
	OneTimeChecksum(final Digest pDigest, final Future<byte[]> pFuture) {
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
		} catch (InterruptedException | ExecutionException e) {
			throw new ChecksumException(e.getMessage(), e);
		}
	}

	@Override
	public void cancel() {
		if (digest != null) {
			digest.cancel();
		}
	}
}