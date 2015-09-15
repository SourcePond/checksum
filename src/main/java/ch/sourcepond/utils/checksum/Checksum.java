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
package ch.sourcepond.utils.checksum;

import java.io.IOException;
import java.security.MessageDigest;

/**
 * Represents a checksum based on a specific hashing algorithm. See
 * {@link MessageDigest} for further information.
 *
 */
public interface Checksum {

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
	 * further information.
	 * 
	 * @return The calculated checksum as byte array, never {@code null}
	 * @throws IOException
	 *             Thrown, if the necessary data could not read from its source
	 *             for any reason.
	 * @throws InterruptedException
	 *             Thrown, if the calculation of the checksum has been
	 *             interrupted.
	 */
	byte[] getValue() throws IOException, InterruptedException;

	/**
	 * Calculates the checksum and returns the result as hex-string. See
	 * {@link #getValue()} for further information.
	 * 
	 * @return The calculated checksum as hex-string, never {@code null}
	 * @throws IOException
	 *             Thrown, if the necessary data could not read from its source
	 *             for any reason.
	 * @throws InterruptedException
	 *             Thrown, if the calculation of the checksum has been
	 *             interrupted.
	 */
	String getHexValue() throws IOException, InterruptedException;
}
