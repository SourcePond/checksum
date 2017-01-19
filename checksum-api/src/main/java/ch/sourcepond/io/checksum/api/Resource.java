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

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * A resource represents an abstraction layer for checksum calculation on some content.
 * If a change on the content (the "source", see {@link #getSource()}) object is detected,
 * the observing client should call one of the {@code update} method on this interface. This will
 * trigger a new {@link Checksum} being calculated which then can be accessed through {@link Future#get()} or
 * through an {@link SuccessObserver}. It's also possible to register observers for cancel and failure
 * cases (see {@link CancelObserver} and {@link FailureObserver}).
 * </p>
 *
 * <p>
 * The used algorithm {@link #getAlgorithm()} and the source {@link #getSource()} uniquely identify
 * a resource. This means that for a particular combination of those attributes exactly one
 * resource exists in the {@link ResourcesRegistry}.
 * </p>
 *
 * <p>When a resource is not needed anymore by the client, it should should release the resource
 * through its {@link #release()} method.</p>
 *
 * @param <S> Type of the source object, i.e. type of the object being observed through this resource
 *            (check the second argument of the {@code get} methods on {@link ResourcesRegistry}).
 */
public interface Resource<S> {

    /**
     * Releases this resource. If this object is not referenced by any client anymore,
     * the {@link ResourcesRegistry} will dispose it freeing all held system resources like buffers etc..
     */
    void release();

    /**
     * Returns the source object where the data to be digested is read from.
     *
     * @return Source object, never {@code null}
     */
    S getSource();

    /**
     * The algorithm being used for checksum calculation.
     *
     * @return Algorithm, never {@code null}
     */
    Algorithm getAlgorithm();

    /**
     * Adds the observer specified to this checksum object. If the observer
     * specified is already registered nothing happens.
     *
     * @param pObserver Observer to be added to this checksum, must not be {@code null}.
     * @return Returns this checksum object, never {@code null}
     * @throws NullPointerException Thrown, if the observer is {@code null}
     */
    Resource<S> addCancelObserver(CancelObserver<S> pObserver);

    /**
     * Adds the observer specified to this checksum object. If the observer
     * specified is already registered nothing happens.
     *
     * @param pObserver Observer to be added to this checksum, must not be {@code null}.
     * @return Returns this checksum object, never {@code null}
     * @throws NullPointerException Thrown, if the observer is {@code null}
     */
    Resource<S> addFailureObserver(FailureObserver<S> pObserver);

    /**
     * Adds the observer specified to this checksum object. If the observer
     * specified is already registered nothing happens.
     *
     * @param pObserver Observer to be added to this checksum, must not be {@code null}.
     * @return Returns this checksum object, never {@code null}
     * @throws NullPointerException Thrown, if the observer is {@code null}
     */
    Resource<S> addSuccessObserver(SuccessObserver<S> pObserver);

    /**
     * Removes the observer specified from this checksum object. If the observer
     * is not registered or is {@code null} nothing happens.
     *
     * @param pObserverOrNull Observer to be removed from this checksum or {@code null}.
     * @return Returns this checksum object, never {@code null}
     */
    Resource<S> removeCancelObserver(CancelObserver<S> pObserverOrNull);

    /**
     * Removes the observer specified from this checksum object. If the observer
     * is not registered or is {@code null} nothing happens.
     *
     * @param pObserverOrNull Observer to be removed from this checksum or {@code null}.
     * @return Returns this checksum object, never {@code null}
     */
    Resource<S> removeFailureObserver(FailureObserver<S> pObserverOrNull);

    /**
     * Removes the observer specified from this checksum object. If the observer
     * is not registered or is {@code null} nothing happens.
     *
     * @param pObserverOrNull Observer to be removed from this checksum or {@code null}.
     * @return Returns this checksum object, never {@code null}
     */
    Resource<S> removeSuccessObserver(SuccessObserver<S> pObserverOrNull);

    /**
     * Short-hand method for {@code update(0, TimeUnit.MILLISECONDS)}.
     *
     * @return Returns a future representing the newly calculated checksum object, never {@code null}
     * @throws RejectedExecutionException Thrown, if the asynchronous update task could not be
     *                                    submitted.
     */
    Future<Checksum> update();

    /**
     * Short-hand method for {@code update(long, TimeUnit.MILLISECONDS)}.
     *
     * @param pIntervalInMilliseconds Time to wait in milliseconds.
     * @return Returns a future representing the newly calculated checksum object, never {@code null}
     * @throws RejectedExecutionException Thrown, if the asynchronous update task could not be
     *                                    submitted.
     * @throws IllegalArgumentException   Thrown, if the interval specified is negative.
     */
    Future<Checksum> update(long pIntervalInMilliseconds);

    /**
     * <p>
     * Updates this checksum in an asynchronous manner. After the new checksum
     * has been calculated, the registered {@link SuccessObserver} will be
     * informed. If the update has been cancelled through {@link Future#cancel(boolean)} with
     * argument {@code true} then all registered {@link CancelObserver} will be informed. In case of
     * a failure because the data necessary could not be read from the source, the causing
     * {@link java.io.IOException} will be passed to all registered {@link FailureObserver} instances.
     * </p>

     * <p>
     * If the end of the data-source has been reached, the update process waits
     * until the interval specified elapses. If more data is available, the
     * data-source will not be closed and the newly available data will be
     * digested. This happens until no more data can be read i.e. the interval
     * elapses and no more data is available.
     * </p>
     *
     * @param pUnit     Time-unit of the interval specified.
     * @param pInterval Time to wait until the data-source should be closed when
     *                  currently no more data is available. Must not be negative, 0
     *                  indicates no wait.
     * @return Returns a future representing the newly calculated checksum object, never {@code null}
     * @throws RejectedExecutionException Thrown, if the asynchronous update task could not be
     *                                    submitted.
     * @throws NullPointerException       Thrown, if the time-unit specified is {@code null}
     * @throws IllegalArgumentException   Thrown, if the interval specified is negative.
     */
    Future<Checksum> update(TimeUnit pUnit, long pInterval);
}
