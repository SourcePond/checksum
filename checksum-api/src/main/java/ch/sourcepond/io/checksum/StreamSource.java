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
import java.io.InputStream;

/**
 * Implementations of this interface allow to open an {@link InputStream} from a
 * specific source. Use implementations of this interface in conjunction with
 * {@link ChecksumFactory#create(String, StreamSource)} to create
 * {@link Checksum} instances which fetch their data from a custom source.
 *
 */
public interface StreamSource {

	/**
	 * Opens an input-stream to the underlying source of this object. Multiple
	 * calls to this method will open individual {@link InputStream} instances.
	 * 
	 * @return {@link InputStream} instance, never {@code null}
	 * @throws IOException
	 *             Thrown, if the input-stream could not be opened for some
	 *             reason.
	 */
	InputStream openStream() throws IOException;
}
