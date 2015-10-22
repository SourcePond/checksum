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
package ch.sourcepond.io.checksum;

import java.io.IOException;
import java.security.MessageDigest;

/**
 * Represents a checksum based on a specific hashing algorithm. See
 * {@link MessageDigest} for further information.
 *
 */
public interface Checksum {

	/**
	 * Cancels any ongoing calculation on this checksum object. If a calculation
	 * is ongoing, it will be cancelled, and, this object will remain in the
	 * state before the calculation has been started. If no calculation is
	 * running, nothing happens.
	 */
	void cancel();

	/**
	 * Returns the algorithm name used to calculate this checksum.
	 * 
	 * @return Algorithm name, never {@code null}
	 */
	String getAlgorithm();

	/**
	 * Calculates the checksum and returns the result as byte array. The length
	 * of the array depends on the used hashing algorithm. See
	 * <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/
	 * StandardNames.html#MessageDigest">MessageDigest Algorithms</a> for
	 * further information. If the calculation of this checksum has been
	 * cancelled through {@link #cancel()} before it was done, this method
	 * returns an empty array.
	 * 
	 * @return The calculated checksum as byte array, never {@code null}
	 * @throws ChecksumException
	 *             Thrown, if the necessary data could not read from its source
	 *             for any reason, the calculating thread has been interrupted,
	 *             or another unexpected exception has occurred.
	 */
	byte[] getValue() throws ChecksumException;

	/**
	 * Calculates the checksum and returns the result as hex-string. See
	 * {@link #getValue()} for further information. If the calculation of this
	 * checksum has been cancelled through {@link #cancel()} before it was done,
	 * this method returns an empty string.
	 * 
	 * @return The calculated checksum as hex-string, never {@code null}
	 * @throws IOException
	 *             Thrown, if the necessary data could not read from its source
	 *             for any reason.
	 * @throws ChecksumException
	 *             Thrown, if the necessary data could not read from its source
	 *             for any reason, the calculating thread has been interrupted,
	 *             or another unexpected exception has occurred.
	 */
	String getHexValue() throws ChecksumException;
}
