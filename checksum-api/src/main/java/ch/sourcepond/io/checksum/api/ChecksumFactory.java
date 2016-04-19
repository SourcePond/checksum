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
package ch.sourcepond.io.checksum.api;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * Factory to create new {@link Checksum} instances. This interface is the
 * entry-point to use the checksum API.
 *
 */
public interface ChecksumFactory {

	/**
	 * OSGi service attribute-name to mark an {@link ExecutorService} as update-
	 * executor. The update executor is used when any the {@code update} methods
	 * on {@link Checksum} is called.
	 */
	String UPDATE_EXECUTOR_ATTRIBUTE = "sourcepond.io.checksum.updateexecutor";

	/**
	 * OSGi service attribute-name to mark an {@link ExecutorService} as
	 * listener- executor. The listener-executor is used when the
	 * {@link UpdateObserver} instances are informed after an update. Every
	 * method call to {@link UpdateObserver} is wrapped into its own
	 * {@link Runnable}.
	 */
	String LISTENER_EXECUTOR_ATTRIBUTE = "sourcepond.io.checksum.listenerexecutor";

	/**
	 * <p>
	 * Creates a new {@link Checksum} instance. The necessary data is read from
	 * the {@link InputStream} returned by {@link StreamSource#openStream()}.
	 * </p>
	 * 
	 * <p>
	 * Note: the returned checksum is not updated yet. You need to call one of
	 * the {@code update} methods on {@link Checksum} before its first usage.
	 * </p>
	 * 
	 * @param pAlgorithm
	 *            The algorithm to be used for checksum calculation, see
	 *            {@link Checksum#getAlgorithm()}
	 * @param pSource
	 *            The source where to read the data to be digested from, must
	 *            not be {@code null}.
	 * @return New {@link Checksum} instance, never {@code null}
	 * @throws RejectedExecutionException
	 *             Thrown, if the asynchronous update task could not be
	 *             submitted.
	 */
	Checksum create(Algorithm pAlgorithm, StreamSource pSource);

	/**
	 * <p>
	 * Creates a new {@link Checksum} instance. The necessary data is read from
	 * the path specified. If the path is a directory, any contained data file
	 * will be digested. Sub-directories will be scanned recursively. If the
	 * path is a regular file, its content will be digested.
	 * </p>
	 * 
	 * <p>
	 * Note: the returned checksum is not updated yet. You need to call one of
	 * the {@code update} methods on {@link Checksum} before its first usage.
	 * </p>
	 * 
	 * @param pAlgorithm
	 *            The algorithm to be used for checksum calculation, see
	 *            {@link Checksum#getAlgorithm()}
	 * @param pPath
	 *            Path to the file or directory to be digested, must not be
	 *            {@code null}.
	 * @return New {@link Checksum} instance, never {@code null}
	 * @throws RejectedExecutionException
	 *             Thrown, if the asynchronous update task could not be
	 *             submitted.
	 */
	Checksum create(Algorithm pAlgorithm, Path pPath);

	/**
	 * <p>
	 * Creates a new {@link Checksum} instance. The necessary data is read from
	 * the {@link URL} specified.
	 * </p>
	 * 
	 * <p>
	 * Note: the returned checksum is not updated yet. You need to call one of
	 * the {@code update} methods on {@link Checksum} before its first usage.
	 * </p>
	 *
	 * @param pAlgorithm
	 *            The algorithm to be used for checksum calculation, see
	 *            {@link Checksum#getAlgorithm()}
	 * @param pUrl
	 *            {@link URL} of the content to be digested, must not be
	 *            {@code null}.
	 * @return New {@link Checksum} instance, never {@code null}
	 * @throws RejectedExecutionException
	 *             Thrown, if the asynchronous update task could not be
	 *             submitted.
	 */
	Checksum create(Algorithm pAlgorithm, URL pUrl);

	/**
	 * <p>
	 * Creates a new {@link Checksum} instance. The necessary data is read from
	 * the {@link InputStream} returned by {@link StreamSource#openStream()}.
	 * </p>
	 * 
	 * <p>
	 * Note: the returned checksum is not updated yet. You need to call one of
	 * the {@code update} methods on {@link Checksum} before its first usage.
	 * </p>
	 * 
	 * @param pAlgorithm
	 *            The algorithm to be used for checksum calculation, see
	 *            {@link Checksum#getAlgorithm()}
	 * @param pSource
	 *            The source where to read the data to be digested from, must
	 *            not be {@code null}.
	 * @return New {@link Checksum} instance, never {@code null}
	 * @throws NoSuchAlgorithmException
	 *             Thrown, if the hashing algorithm specified is unknown.
	 * @throws RejectedExecutionException
	 *             Thrown, if the asynchronous update task could not be
	 *             submitted.
	 */
	Checksum create(String pAlgorithm, StreamSource pSource) throws NoSuchAlgorithmException;

	/**
	 * <p>
	 * Creates a new {@link Checksum} instance. The necessary data is read from
	 * the path specified. If the path is a directory, any contained data file
	 * will be digested. Sub-directories will be scanned recursively. If the
	 * path is a regular file, its content will be digested.
	 * </p>
	 * 
	 * <p>
	 * Note: the returned checksum is not updated yet. You need to call one of
	 * the {@code update} methods on {@link Checksum} before its first usage.
	 * </p>
	 * 
	 * @param pAlgorithm
	 *            The algorithm to be used for checksum calculation, see
	 *            {@link Checksum#getAlgorithm()}
	 * @param pPath
	 *            Path to the file or directory to be digested, must not be
	 *            {@code null}.
	 * @return New {@link Checksum} instance, never {@code null}
	 * @throws NoSuchAlgorithmException
	 *             Thrown, if the hashing algorithm specified is unknown.
	 * @throws RejectedExecutionException
	 *             Thrown, if the asynchronous update task could not be
	 *             submitted.
	 */
	Checksum create(String pAlgorithm, Path pPath) throws NoSuchAlgorithmException;

	/**
	 * <p>
	 * Creates a new {@link Checksum} instance. The necessary data is read from
	 * the {@link URL} specified.
	 * </p>
	 * 
	 * <p>
	 * Note: the returned checksum is not updated yet. You need to call one of
	 * the {@code update} methods on {@link Checksum} before its first usage.
	 * </p>
	 *
	 * @param pAlgorithm
	 *            The algorithm to be used for checksum calculation, see
	 *            {@link Checksum#getAlgorithm()}
	 * @param pUrl
	 *            {@link URL} of the content to be digested, must not be
	 *            {@code null}.
	 * @return New {@link Checksum} instance, never {@code null}
	 * @throws NoSuchAlgorithmException
	 *             Thrown, if the hashing algorithm specified is unknown.
	 * @throws RejectedExecutionException
	 *             Thrown, if the asynchronous update task could not be
	 *             submitted.
	 */
	Checksum create(String pAlgorithm, URL pUrl) throws NoSuchAlgorithmException;
}
