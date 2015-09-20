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
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * Builder to create new {@link Checksum} or {@link PathChecksum} with a
 * specific hashing algorithm .
 */
public interface ChecksumBuilder {

	/**
	 * Creates a new immutable {@link Checksum} instance. The necessary data is
	 * read from {@link InputStream} specified. This method will close the
	 * stream when the calculation is done. The calculation of the checksum will
	 * be performed <em>synchronously</em>, i.e. this method blocks until the
	 * calculation process finishes.
	 * 
	 * @param pInputStream
	 *            The input-stream from where to read the data to be digested,
	 *            must not be {@code null}.
	 * @return New {@link Checksum} instance, never {@code null}.
	 * @throws IOException
	 *             Thrown, if the data could not be read from the input-stream
	 *             specified.
	 */
	Checksum create(InputStream pInputStream) throws IOException;

	/**
	 * Creates a new immutable {@link Checksum} instance. The necessary data is
	 * read from {@link InputStream} specified. This method will close the
	 * stream when the calculation is done. The calculation of the checksum will
	 * be performed <em>asynchronously</em> with the executor specified.
	 * 
	 * @param pInputStream
	 *            The input-stream from where to read the data to be digested,
	 *            must not be {@code null}.
	 * @param pExecutor
	 *            The executor to be used to calculate the checksum; must not be
	 *            {@code null}.
	 * @return New {@link Checksum} instance, never {@code null}.
	 * @throws IOException
	 *             Thrown, if the data could not be read from the input-stream
	 *             specified.
	 */
	Checksum create(InputStream pInputStream, ExecutorService pExecutor) throws IOException;

	/**
	 * <p>
	 * Creates a new {@link PathChecksum} instance. The necessary data is read
	 * from path specified. If the path is a directory, any contained data file
	 * will be digested. Sub-directories will be scanned recursively. If the
	 * path is a regular file, its content will be digested.
	 * </p>
	 * 
	 * <p>
	 * The calculation of the checksum (see {@link PathChecksum#update()}) will
	 * be performed <em>synchronously</em>, i.e. this method blocks until the
	 * calculation process finishes.
	 * </p>
	 * 
	 * @param pPath
	 *            Path to the file or directory to be digested, must not be
	 *            {@code null}.
	 * @return New {@link PathChecksum} instance, never {@code null}
	 * @throws ChecksumException
	 *             Thrown, if the necessary data could not read from its source
	 *             for any reason, the calculating thread has been interrupted,
	 *             or another unexpected exception has occurred.
	 */
	PathChecksum create(Path pPath) throws ChecksumException;

	/**
	 * <p>
	 * Creates a new {@link PathChecksum} instance. The necessary data is read
	 * from path specified. If the path is a directory, any contained data file
	 * will be digested. Sub-directories will be scanned recursively. If the
	 * path is a regular file, its content will be digested.
	 * </p>
	 * 
	 * <p>
	 * The calculation of the checksum (see {@link PathChecksum#update()}) will
	 * be performed <em>asynchronously</em> with the executor specified.
	 * </p>
	 * 
	 * @param pPath
	 *            Path to the file or directory to be digested, must not be
	 *            {@code null}.
	 * @param pExecutor
	 *            The executor to be used to calculate the checksum. Should also
	 *            be used by {@link PathChecksum#update()}. Must not be
	 *            {@code null}.
	 * @return New {@link PathChecksum} instance, never {@code null}
	 * @throws ChecksumException
	 *             Thrown, if the necessary data could not read from its source
	 *             for any reason, the calculating thread has been interrupted,
	 *             or another unexpected exception has occurred.
	 */
	PathChecksum create(Path pPath, ExecutorService pExecutor) throws ChecksumException;
}
