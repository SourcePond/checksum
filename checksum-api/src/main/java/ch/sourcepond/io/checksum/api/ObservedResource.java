package ch.sourcepond.io.checksum.api;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public interface ObservedResource<T> {

    void dispose();

    /**
     * Adds the observer specified to this checksum object. If the observer
     * specified is already registered nothing happens.
     *
     * @param pObserver Observer to be added to this checksum, must not be {@code null}.
     * @return Returns this checksum object, never {@code null}
     * @throws NullPointerException Thrown, if the observer is {@code null}
     */
    ObservedResource<T> addUpdateObserver(UpdateObserver<T> pObserver);

    /**
     * Removes the observer specified from this checksum object. If the observer
     * is not registered or is {@code null} nothing happens.
     *
     * @param pObserverOrNull Observer to be removed from this checksum or {@code null}.
     * @return Returns this checksum object, never {@code null}
     * @throws NullPointerException Thrown, if the observer is {@code null}
     */
    ObservedResource<T> removeUpdateObserver(UpdateObserver<T> pObserverOrNull);

    /**
     * Cancels any ongoing calculation which has started through
     * {@link #update()} on this object. If a calculation is ongoing, it will be
     * cancelled, and, this object will remain in the state before the
     * calculation has been started. If no calculation is running, nothing
     * happens.
     *
     * @return Returns this checksum object, never {@code null}
     */
    ObservedResource<T> cancel();

    /**
     * Short-hand method for {@code update(0, TimeUnit.MILLISECONDS)}.
     *
     * @return Returns this checksum object, never {@code null}
     * @throws RejectedExecutionException
     *             Thrown, if the asynchronous update task could not be
     *             submitted.
     */
    ObservedResource<T> update();

    /**
     * Short-hand method for {@code update(long, TimeUnit.MILLISECONDS)}.
     *
     * @param pIntervalInMilliseconds
     *            Time to wait in milliseconds.
     * @return Returns this checksum object, never {@code null}
     * @throws RejectedExecutionException
     *             Thrown, if the asynchronous update task could not be
     *             submitted.
     */
    ObservedResource<T> update(long pIntervalInMilliseconds);

    /**
     * <p>
     * Updates this checksum in a non-blocking manner. After the new checksum
     * has been calculated, the old checksum will be saved and can later be
     * accessed through {@link #getPreviousValue()} or
     * {@link #getPreviousHexValue()}. The newly calculated checksum can be
     * accessed through {@link #getValue()} or {@link #getHexValue()}.
     * </p>
     *
     * <p>
     * If the end of the data-source has been reached, the update process waits
     * until the interval specified elapses. If more data is available, the
     * data-source will not be closed and the newly available data will be
     * digested. This happens until no more data can be read i.e. the interval
     * elapses and no more data is available.
     * </p>
     *
     * @param pInterval
     *            Time to wait until the data-source should be closed when
     *            currently no more data is available. Must not be negative, 0
     *            indicates no wait.
     * @param pUnit
     *            Time-unit of the interval specified.
     * @return Returns this checksum object, never {@code null}
     * @throws RejectedExecutionException
     *             Thrown, if the asynchronous update task could not be
     *             submitted.
     */
    ObservedResource<T> update(long pInterval, TimeUnit pUnit);
}
