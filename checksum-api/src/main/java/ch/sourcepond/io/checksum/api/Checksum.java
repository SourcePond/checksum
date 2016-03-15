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
import java.security.MessageDigest;
import java.util.concurrent.RejectedExecutionException;

/**
 * Abstracts a checksum based on a specific hashing algorithm (see
 * {@link MessageDigest} for further information) and provides the ability to
 * calculate, query and update the represented checksum value.
 */
public interface Checksum {

	/**
	 * Cancels any ongoing calculation which has started through
	 * {@link #update()} on this object. If a calculation is ongoing, it will be
	 * cancelled, and, this object will remain in the state before the
	 * calculation has been started. If no calculation is running, nothing
	 * happens.
	 */
	void cancel();

	/**
	 * <p>
	 * Updates this checksum. After the new checksum has been calculated, the
	 * old checksum will be saved and can later be accessed through
	 * {@link #getPreviousValue()} or {@link #getPreviousHexValue()}. The newly
	 * calculated checksum can be accessed through {@link #getValue()} or
	 * {@link #getHexValue()}.
	 * </p>
	 * 
	 * <p>
	 * The new checksum can be accessed through {@link #getValue()} or
	 * {@link #getHexValue()}.
	 * </p>
	 * 
	 * @throws RejectedExecutionException
	 *             Thrown, if the asynchronous update task could not be
	 *             submitted.
	 */
	void update();

	/**
	 * Checks whether an update is currently running (started through
	 * {@link #update()}).
	 * 
	 * @return {@code true} if an update is running, {@code false} otherwise.
	 */
	boolean isUpdating();

	/**
	 * Returns the algorithm name used to calculate this checksum See
	 * <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/
	 * StandardNames.html#MessageDigest">MessageDigest Algorithms</a> for
	 * further information.
	 * 
	 * @return Algorithm name, never {@code null}
	 */
	String getAlgorithm();

	/**
	 * <p>
	 * Gets the result of the latest completely done calculation triggered
	 * through {@link #update()}. If the latest calculation has been failed, the
	 * causing exception will be re-thrown.
	 * </p>
	 * 
	 * <p>
	 * If the latest calculation was successful, the checksum will be returned
	 * as byte array. The length of the array depends on the used hashing
	 * algorithm (see
	 * <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/
	 * StandardNames.html#MessageDigest">MessageDigest Algorithms</a> for
	 * further information). Note: the returned array is a copy i.e. changing
	 * the returned value will have no effect on the internal state of this
	 * object.
	 * </p>
	 * 
	 * <p>
	 * If {@link #update()} has never been called an empty array will be
	 * returned.
	 * </p>
	 * 
	 * @return The calculated checksum as byte array, never {@code null}
	 * @throws InterruptedException
	 *             Thrown, if the latest {@link #update()} operation has been
	 *             interrupted (see {@link #cancel()}).
	 * @throws ChecksumException
	 *             Thrown, if the latest {@link #update()} operation has been
	 *             failed (necessary data could not read from its source for any
	 *             reason, the calculating thread has been interrupted, or
	 *             another unexpected exception has occurred)
	 */
	byte[] getValue() throws InterruptedException, ChecksumException;

	/**
	 * Returns the checksum as hex-string. See {@link #getValue()} for further
	 * information.
	 * 
	 * @return The calculated checksum as string, never {@code null}
	 * @throws InterruptedException
	 *             Thrown, if the latest {@link #update()} operation has been
	 *             interrupted (see {@link #cancel()}).
	 * @throws ChecksumException
	 *             Thrown, if the latest {@link #update()} operation has been
	 *             failed (necessary data could not read from its source for any
	 *             reason, the calculating thread has been interrupted, or
	 *             another unexpected exception has occurred)
	 */
	String getHexValue() throws InterruptedException, ChecksumException;

	/**
	 * Checks whether the current checksum, i.e. the resulting checksum
	 * <em>after</em> the latest {@link #update()} has been
	 * <em>successfully</em> performed, is equal to the previous checksum, i.e.
	 * the last <em>successfully</em> calculated checksum <em>before</em> the
	 * latest {@link #update()} has been performed.
	 * 
	 * @return {@code true} if the current and previous checksum are equal,
	 *         {@code false} otherwise.
	 */
	boolean equalsPrevious();

	/**
	 * Returns the previous checksum before the latest {@link #update()} has
	 * been performed. If {@link #update()} has never been called an empty array
	 * will be returned.
	 * 
	 * @return Previous checksum as byte array, never {@code null}
	 */
	byte[] getPreviousValue();

	/**
	 * Returns the previous checksum before the last {@link #update()} occurred
	 * as hex-string. If {@link #update()} has not been called more than once,
	 * an empty string will be returned.
	 * 
	 * @return Previous checksum as hex-string, never {@code null}
	 * @throws IOException
	 *             Thrown, if the necessary data could not be read for some
	 *             reason.
	 */
	String getPreviousHexValue();
}
