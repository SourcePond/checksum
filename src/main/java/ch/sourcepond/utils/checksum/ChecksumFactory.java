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
import java.io.InputStream;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;

/**
 * Factory to create new {@link Checksum} or {@link PathChecksum} instances.
 *
 */
public interface ChecksumFactory {

	/**
	 * Creates a new immutable {@link Checksum} instance. The necessary data is
	 * read from {@link InputStream} specified. The stream will <em>not</em> be
	 * closed by this method. The calculation of the checksum will be performed
	 * synchronously, i.e. this method blocks until the calculation process
	 * finishes.
	 * 
	 * @param pInputStream
	 *            The input-stream from where to read the data to be digested,
	 *            must not be {@code null}.
	 * @param pAlgorithm
	 *            The name of the algorithm requested. See the MessageDigest
	 *            section in the <a href=
	 *            "{@docRoot}/../technotes/guides/security/StandardNames.html#MessageDigest"
	 *            > Java Cryptography Architecture Standard Algorithm Name
	 *            Documentation</a> for information about standard algorithm
	 *            names. Must not be {@code null}
	 * @return New {@link Checksum} instance, never {@code null}.
	 * @throws NoSuchAlgorithmException
	 *             Thrown, if no provider supports a MessageDigestSpi
	 *             implementation for the specified algorithm.
	 * @throws IOException
	 *             Thrown, if the data could not be read from the input-stream
	 *             specified.
	 */
	Checksum create(InputStream pInputStream, String pAlgorithm) throws NoSuchAlgorithmException, IOException;

	/**
	 * @param pPath
	 * @param pAlgorithm
	 * @return
	 * @throws IOException
	 */
	PathChecksum create(Path pPath, String pAlgorithm) throws NoSuchAlgorithmException, IOException;

	/**
	 * @param pPath
	 * @return
	 */
	PathChecksum create(ExecutorService pCalculator, Path pPath, String pAlgorithm)
			throws NoSuchAlgorithmException, IOException;
}
