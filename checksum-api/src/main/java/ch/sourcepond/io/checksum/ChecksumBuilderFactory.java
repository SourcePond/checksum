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

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;

/**
 * Factory to create new {@link Checksum} or {@link UpdatableChecksum}
 * instances.
 *
 */
public interface ChecksumBuilderFactory {

	/**
	 * Constant for MD5 algorithm.
	 */
	String MD5 = "MD5";

	/**
	 * Constant for SHA-256 algorithm.
	 */
	String SHA256 = "SHA-256";

	/**
	 * Constant for SHA-384 algorithm.
	 */
	String SHA384 = "SHA-384";

	/**
	 * Constant for SHA-512 algorithm.
	 */
	String SHA512 = "SHA-512";

	/**
	 * Creates a new {@link ChecksumBuilder} with the hashing algorithm
	 * specified.
	 * 
	 * @param pExecutor
	 *            The executor to be used to perform checksum calculations. Must
	 *            not be {@code null}.
	 * @param pAlgorithm
	 *            The name of the algorithm requested. See the MessageDigest
	 *            section in the <a href=
	 *            "http://docs.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#provider"
	 *            > Java Cryptography Architecture Standard Algorithm Name
	 *            Documentation</a> for information about standard algorithm
	 *            names. Must not be {@code null}
	 * @return New builder instance, never {@code null}.
	 * @throws NoSuchAlgorithmException
	 *             Thrown, if no provider supports a MessageDigestSpi
	 *             implementation for the specified algorithm.
	 * 
	 */
	ChecksumBuilder create(ExecutorService pExecutor, String pAlgorithm) throws NoSuchAlgorithmException;
}
