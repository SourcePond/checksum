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

import java.net.URL;
import java.nio.file.Path;

/**
 * Registry from where to retrieve existing or new {@link Resource} instances. This interface is the
 * entry-point to use the checksum API.
 *
 */
@SuppressWarnings("ALL")
public interface ResourcesRegistry {

	/**
	 * <p>
	 * Returns the registered {@link Resource} for the algorithm and source specified. If no resource is
     * registered yet, a new instance will be created and stored for subsequent access.
	 * </p>
	 *
	 * <p>
	 * Note: the returned resource is not updated yet. You need to call one of
	 * the {@code update} methods before its first usage.
	 * </p>
	 *
	 * @param pAlgorithm
	 *            The algorithm to be used for checksum calculation, see
	 *            {@link Resource#getAlgorithm()}
	 * @param pSource
	 *            The source where to read the data to be digested from, must
	 *            not be {@code null}.
	 * @return Resource instance, never {@code null}
	 * @throws NullPointerException Thrown, if either the algorithm or the source specified is {@code null}.
	 */
	Resource<ChannelSource> get(Algorithm pAlgorithm, ChannelSource pSource);

    /**
     * <p>
     * Returns the registered {@link Resource} for the algorithm and source specified. If no resource is
     * registered yet, a new instance will be created and stored for subsequent access.
     * </p>
     *
     * <p>
     * Note: the returned resource is not updated yet. You need to call one of
     * the {@code update} methods before its first usage.
     * </p>
     *
     * @param pAlgorithm
     *            The algorithm to be used for checksum calculation, see
     *            {@link Resource#getAlgorithm()}
     * @param pSource
     *            The source where to read the data to be digested from, must
     *            not be {@code null}.
     * @return Resource instance, never {@code null}
     * @throws NullPointerException Thrown, if either the algorithm or the source specified is {@code null}.
     */
	Resource<StreamSource> get(Algorithm pAlgorithm, StreamSource pSource);

    /**
     * <p>
     * Returns the registered {@link Resource} for the algorithm and source specified. If no resource is
     * registered yet, a new instance will be created and stored for subsequent access.
     * </p>
     *
     * <p>
     * Note: the returned resource is not updated yet. You need to call one of
     * the {@code update} methods before its first usage.
     * </p>
     *
     * @param pAlgorithm
     *            The algorithm to be used for checksum calculation, see
     *            {@link Resource#getAlgorithm()}
     * @param pSource
     *            The source where to read the data to be digested from, must
     *            not be {@code null}.
     * @return Resource instance, never {@code null}
     * @throws NullPointerException Thrown, if either the algorithm or the source specified is {@code null}.
     */
	Resource<Path> get(Algorithm pAlgorithm, Path pSource);

    /**
     * <p>
     * Returns the registered {@link Resource} for the algorithm and source specified. If no resource is
     * registered yet, a new instance will be created and stored for subsequent access.
     * </p>
     *
     * <p>
     * Note: the returned resource is not updated yet. You need to call one of
     * the {@code update} methods before its first usage.
     * </p>
     *
     * @param pAlgorithm
     *            The algorithm to be used for checksum calculation, see
     *            {@link Resource#getAlgorithm()}
     * @param pSource
     *            The source where to read the data to be digested from, must
     *            not be {@code null}.
     * @return Resource instance, never {@code null}
     * @throws NullPointerException Thrown, if either the algorithm or the source specified is {@code null}.
     */
	Resource<URL> get(Algorithm pAlgorithm, URL pSource);
}
