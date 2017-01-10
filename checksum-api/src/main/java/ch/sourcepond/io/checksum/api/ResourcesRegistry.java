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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * Registry from where to retrieve existing or new {@link Resource} instances. This interface is the
 * entry-point to use the checksum API.
 *
 */
public interface ResourcesRegistry {

	/**
	 * OSGi service attribute-name to mark an {@link ExecutorService} as update-
	 * executor. The update executor is used when any the {@code update} methods
	 * on {@link Resource} is called.
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
	 * the {@link InputStream} returned by {@link ChannelSource#openChannel()}.
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
	 * @return New {@link Resource} instance, never {@code null}
	 * @throws NullPointerException Thrown, if either the algorithm or the stream-source specified is {@code null}.
	 * @throws RejectedExecutionException
	 *             Thrown, if the asynchronous update task could not be
	 *             submitted.
	 */
	Resource<ChannelSource> get(Algorithm pAlgorithm, ChannelSource pSource);

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
	 * @return New {@link Resource} instance, never {@code null}
	 * @throws NullPointerException Thrown, if either the algorithm or the stream-source specified is {@code null}.
	 * @throws RejectedExecutionException
	 *             Thrown, if the asynchronous update task could not be
	 *             submitted.
	 */
	Resource<StreamSource> get(Algorithm pAlgorithm, StreamSource pSource);

	/**
	 * <p>
	 * Creates a new {@link Checksum} instance. The necessary data is read from
	 * the file specified. The path must be a regular file otherwise an {@link IOException} will be
	 * caused to be thrown.
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
	 * @return New {@link Resource} instance, never {@code null}
	 * @throws NullPointerException Thrown if either the algorithm or the file specified is {@code null}.
	 * @throws IOException Thrown if the path specified is not a regular file.
	 */
	Resource<Path> get(Algorithm pAlgorithm, Path pPath) throws IOException;

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
	 * @return New {@link Resource} instance, never {@code null}
	 * @throws NullPointerException Thrown, if either the algorithm or the url specified is {@code null}.
	 * @throws RejectedExecutionException
	 *             Thrown, if the asynchronous update task could not be
	 *             submitted.
	 */
	Resource<URL> get(Algorithm pAlgorithm, URL pUrl);
}
