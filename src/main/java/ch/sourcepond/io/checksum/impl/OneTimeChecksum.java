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

/**
 *
 */
final class OneTimeChecksum extends BaseChecksum {
	private Future<byte[]> future;
	private OneTimeCalculation calculation;
	private volatile byte[] value;
	private final String algorithm;

	/**
	 * @param pValue
	 * @param pAlgorithm
	 */
	OneTimeChecksum(final Future<byte[]> pFuture, final String pAlgorithm) {
		future = pFuture;
		algorithm = pAlgorithm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.Checksum#getAlgorithm()
	 */
	@Override
	public String getAlgorithm() {
		return algorithm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.BaseChecksum#getValueUnsynchronized()
	 */
	@Override
	protected byte[] evaluateValue() throws ChecksumException {
		if (value == null) {
			synchronized (this) {
				if (value == null) {
					try {
						value = future.get();
						if (value == null) {
							value = INITIAL;
						}
					} catch (InterruptedException | ExecutionException e) {
						throw new ChecksumException(e.getMessage(), e);
					}
					future = null;
					calculation = null;
				}
			}
		}
		return value;
	}

	@Override
	public synchronized void cancel() {
		if (calculation != null) {
			calculation.cancel();
			value = INITIAL;
			calculation = null;
			future = null;
		}
	}
}
