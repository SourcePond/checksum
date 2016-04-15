/*Copyright (C) 2016 Roland Hauser, <sourcepond@gmail.com>

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

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public interface UpdateableChecksum extends Checksum {

	void addUpdateObserver(UpdateObserver pObserver);

	void removeUpdateObserver(UpdateObserver pObserver);

	/**
	 * Cancels any ongoing calculation which has started through
	 * {@link #update()} on this object. If a calculation is ongoing, it will be
	 * cancelled, and, this object will remain in the state before the
	 * calculation has been started. If no calculation is running, nothing
	 * happens.
	 * 
	 * @return Returns this checksum object, never {@code null}
	 */
	Checksum cancel();

	/**
	 * Short-hand method for {@code update(0, TimeUnit.MILLISECONDS)}.
	 * 
	 * @return Returns this checksum object, never {@code null}
	 * @throws RejectedExecutionException
	 *             Thrown, if the asynchronous update task could not be
	 *             submitted.
	 */
	Checksum update();

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
	Checksum update(long pIntervalInMilliseconds);

	/**
	 * <p>
	 * Updates this checksum in a non-blocking manner. After the new checksum
	 * has been calculated, the old checksum will be saved and can later be
	 * accessed through {@link #getPreviousValue()} or
	 * {@link #getPreviousHexValue()}. The newly calculated checksum can be
	 * accessed through {@link #getValue()} or {@link #getHexValue()}. If an
	 * update is already running, then nothing happens.
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
	Checksum update(long pInterval, TimeUnit pUnit);

	/**
	 * Checks whether an update is currently running (started through
	 * {@link #update()}).
	 * 
	 * @return {@code true} if an update is running, {@code false} otherwise.
	 */
	boolean isUpdating();
}
