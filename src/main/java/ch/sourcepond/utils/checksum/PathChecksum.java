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
package ch.sourcepond.utils.checksum;

import java.io.IOException;
import java.nio.file.Path;

/**
 * <p>
 * Extension of the {@link Checksum} interface. It allows to calculate the
 * checksum on the content of a {@link Path}. Furthermore, it allows to keep
 * track about changes on the content.
 * </p>
 * 
 * <p>
 * If the path is a directory, any contained data file will be digested.
 * Sub-directories will be scanned recursively. If the path is a regular file,
 * its content will be digested.
 * </p>
 */
public interface PathChecksum extends Checksum {

	/**
	 * Returns the path which is digested by this checksum. The path can be a
	 * directory or a regular file.
	 * 
	 * @return Digested path, never {@code null}.
	 */
	Path getPath();

	/**
	 * Checks whether the current checksum, i.e. the checksum <em>after</em> the
	 * last {@link #update()} has been performed is equal to the previous
	 * checksum, i.e. the checksum <em>before</em> the last {@link #update()}
	 * has been performed.
	 * 
	 * @return {@code true} if the current and previous checksum are equal,
	 *         {@code false} otherwise.
	 * @throws IOException
	 *             Thrown, if the necessary data could not be read for some
	 *             reason.
	 * @throws InterruptedException
	 *             Thrown, if the calculation of the current checksum has been
	 *             interrupted.
	 */
	boolean equalsPrevious() throws IOException, InterruptedException;

	/**
	 * Returns the previous checksum before the last {@link #update()} occurred.
	 * If {@link #update()} has never been called an empty array will be
	 * returned.
	 * 
	 * @return Previous checksum as byte array, never {@code null}
	 */
	byte[] getPreviousValue();

	/**
	 * Returns the previous checksum before the last {@link #update()} occurred
	 * as hex-string. If {@link #update()} has never been called an empty string
	 * will be returned.
	 * 
	 * @return Previous checksum as hex-string, never {@code null}
	 */
	String getPreviousHexValue();

	/**
	 * Updates this checksum. To do this, the content of the path returned by
	 * {@link #getPath()} will be read and digested. After the new checksum has
	 * been calculated, the old checksum will be saved and can later be accessed
	 * through {@link #getPreviousValue()} or {@link #getPreviousHexValue()}.
	 * The newly calculated checksum can be accessed through {@link #getValue()}
	 * or {@link #getHexValue()}.
	 * 
	 * @throws IOException
	 *             Thrown, if the necessary data could not be read for some
	 *             reason.
	 */
	void update() throws IOException;
}
