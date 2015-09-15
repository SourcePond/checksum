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
 * @author rolandhauser
 *
 */
public interface PathChecksum extends Checksum {

	/**
	 * @return
	 */
	Path getPath();

	/**
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	boolean equalsPrevious() throws IOException;

	/**
	 * Returns the previous fingerprint before the last {@link #update()}
	 * occurred. If {@link #update()} has never been called an empty array will
	 * be returned.
	 * 
	 * @return Previous fingerprint, never {@code null}
	 */
	byte[] getPreviousValue();

	/**
	 * Returns the previous fingerprint before the last {@link #update()}
	 * occurred as hex string. If {@link #update()} has never been called an
	 * empty string will be returned.
	 * 
	 * @return Previous fingerprint, never {@code null}
	 */
	String getPreviousValueAsString();

	void update() throws IOException;
}
