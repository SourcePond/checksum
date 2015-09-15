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
package ch.sourcepond.utils.checksum.impl;

/**
 *
 */
final class ImmutableChecksum extends BaseChecksum {
	private final byte[] value;
	private final String algorithm;

	/**
	 * @param pValue
	 * @param pAlgorithm
	 */
	ImmutableChecksum(final byte[] pValue, final String pAlgorithm) {
		value = pValue;
		algorithm = pAlgorithm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.utils.checksum.Checksum#getAlgorithm()
	 */
	@Override
	public String getAlgorithm() {
		return algorithm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.utils.checksum.impl.BaseChecksum#getValueUnsynchronized()
	 */
	@Override
	protected byte[] getValueUnsynchronized() {
		return value;
	}
}
