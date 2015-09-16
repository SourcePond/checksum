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

import static java.lang.System.arraycopy;
import static org.apache.commons.codec.binary.Hex.encodeHexString;

import ch.sourcepond.utils.checksum.Checksum;
import ch.sourcepond.utils.checksum.ChecksumException;

/**
 *
 */
abstract class BaseChecksum implements Checksum {

	/**
	 * @return
	 */
	protected abstract byte[] getValueUnsynchronized();

	/**
	 * @param pOriginal
	 * @return
	 */
	protected final byte[] copyArray(final byte[] pOriginal) {
		final byte[] copy = new byte[pOriginal.length];
		arraycopy(pOriginal, 0, copy, 0, pOriginal.length);
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.utils.checksum.Checksum#getValue()
	 */
	@Override
	public byte[] getValue() throws ChecksumException {
		return copyArray(getValueUnsynchronized());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.utils.checksum.Checksum#getHexValue()
	 */
	@Override
	public String getHexValue() throws ChecksumException {
		return encodeHexString(getValueUnsynchronized());
	}
}
