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
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * A resource represents an abstraction layer for checksum calculation on some content.
 * If a change on the content object is detected,
 * the observing client should call one of the {@code update} method on this interface. This will
 * trigger a new {@link Checksum} being calculated which then can be accessed through {@link Future#get()} or
 * through a {@link UpdateObserver}.
 * </p>
 *
 */
public interface Resource {

    /**
     * The algorithm being used for checksum calculation.
     *
     * @return Algorithm, never {@code null}
     */
    Algorithm getAlgorithm();

    /**
     * Short-hand method for {@code update(0, TimeUnit.MILLISECONDS)}.
     *
     * @return Returns a future representing the newly calculated checksum object, never {@code null}
     * @throws IOException Thrown, if the resource could not be opened for reading.
     * @throws RejectedExecutionException Thrown, if the asynchronous update task could not be
     *                                    submitted.
     */
    Future<Checksum> update(UpdateObserver pObserver) throws IOException;

    /**
     * Short-hand method for {@code update(long, TimeUnit.MILLISECONDS)}.
     *
     * @param pIntervalInMilliseconds Time to wait in milliseconds.
     * @return Returns a future representing the newly calculated checksum object, never {@code null}
     * @throws IOException Thrown, if the resource could not be opened for reading.
     * @throws RejectedExecutionException Thrown, if the asynchronous update task could not be
     *                                    submitted.
     * @throws IllegalArgumentException   Thrown, if the interval specified is negative.
     */
    Future<Checksum> update(long pIntervalInMilliseconds, UpdateObserver pObserver) throws IOException;

    /**
     * <p>
     * Updates this checksum in an asynchronous manner. After the new checksum
     * has been calculated, the {@link UpdateObserver} specified will be
     * informed.
     * </p>

     * <p>
     * If the end of the data-source has been reached, the update process waits
     * until the interval specified elapses. If more data is available, the
     * data-source will not be closed and the newly available data will be
     * digested. This happens until no more data can be read i.e. the interval
     * elapses and no more data is available.
     * </p>
     *
     * <p>If the update fails for some reason, the current checksum will be remain
     * untouched, see {@link Update#hasChanged()}.</p>
     *
     * @param pUnit     Time-unit of the interval specified.
     * @param pInterval Time to wait until the data-source should be closed when
     *                  currently no more data is available. Must not be negative, 0
     *                  indicates no wait.
     * @return Returns a future representing the newly calculated checksum object, never {@code null}
     * @throws IOException Thrown, if the resource could not be opened for reading.
     * @throws RejectedExecutionException Thrown, if the asynchronous update task could not be
     *                                    submitted.
     * @throws NullPointerException       Thrown, if the time-unit specified is {@code null}
     * @throws IllegalArgumentException   Thrown, if the interval specified is negative.
     */
    Future<Checksum> update(TimeUnit pUnit, long pInterval, UpdateObserver pObserver) throws IOException;
}
