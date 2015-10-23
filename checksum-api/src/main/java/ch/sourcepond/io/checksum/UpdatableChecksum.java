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

/**
 * Extension of the {@link Checksum} interface. It allows to calculate the
 * checksum based on the content of a specific source. See {@code create}
 * methods on interface {@link ChecksumBuilder} for supported source types.
 * Furthermore, it provides the ability to update the checksum in case the
 * source content has been changed (see {@link #update()}).
 * 
 * @param <T>
 *            Type of the source object which provides the data to be digested.
 */
public interface UpdatableChecksum<T> extends Checksum {

	/**
	 * Returns the source from where the data is fetched for calculating this
	 * checksum.
	 * 
	 * @return Data source, never {@code null}.
	 */
	T getSource();

	/**
	 * Checks whether the current checksum, i.e. the checksum <em>after</em> the
	 * last {@link #update()} has been performed is equal to the previous
	 * checksum, i.e. the checksum <em>before</em> the last {@link #update()}
	 * has been performed.
	 * 
	 * @return {@code true} if the current and previous checksum are equal,
	 *         {@code false} otherwise.
	 * @throws ChecksumException
	 *             Thrown, if the necessary data could not read from its source
	 *             for any reason, the calculating thread has been interrupted,
	 *             or another unexpected exception has occurred.
	 */
	boolean equalsPrevious() throws ChecksumException;

	/**
	 * Returns the previous checksum before the last {@link #update()} occurred.
	 * If {@link #update()} has never been called an empty array will be
	 * returned.
	 * 
	 * @return Previous checksum as byte array, never {@code null}
	 * @throws IOException
	 *             Thrown, if the necessary data could not be read for some
	 *             reason.
	 * @throws ChecksumException
	 *             Thrown, if the necessary data could not read from its source
	 *             for any reason, the calculating thread has been interrupted,
	 *             or another unexpected exception has occurred.
	 */
	byte[] getPreviousValue() throws ChecksumException;

	/**
	 * Returns the previous checksum before the last {@link #update()} occurred
	 * as hex-string. If {@link #update()} has not been called more than once,
	 * an empty string will be returned.
	 * 
	 * @return Previous checksum as hex-string, never {@code null}
	 * @throws IOException
	 *             Thrown, if the necessary data could not be read for some
	 *             reason.
	 * @throws ChecksumException
	 *             Thrown, if the necessary data could not read from its source
	 *             for any reason, the calculating thread has been interrupted,
	 *             or another unexpected exception has occurred.
	 */
	String getPreviousHexValue() throws ChecksumException;

	/**
	 * Updates this checksum. To do this, the content of the path returned by
	 * {@link #getSource()} will be read and digested. After the new checksum
	 * has been calculated, the old checksum will be saved and can later be
	 * accessed through {@link #getPreviousValue()} or
	 * {@link #getPreviousHexValue()}. The newly calculated checksum can be
	 * accessed through {@link #getValue()} or {@link #getHexValue()}.
	 * 
	 * @throws IOException
	 *             Thrown, if the necessary data could not be read for some
	 *             reason.
	 * @throws ChecksumException
	 *             Thrown, if the necessary data could not read from its source
	 *             for any reason, the calculating thread has been interrupted,
	 *             or another unexpected exception has occurred.
	 */
	void update() throws ChecksumException;
}
