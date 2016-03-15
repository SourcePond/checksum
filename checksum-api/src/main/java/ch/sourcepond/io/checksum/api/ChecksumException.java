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

/**
 * This exception is thrown if any operation during calculating a
 * {@link Checksum} or {@link Checksum} fails for some reason.
 *
 */
public class ChecksumException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1005754965088339032L;

	/**
	 * See {@link Exception#Exception()}
	 */
	public ChecksumException() {
		super();
	}

	/**
	 * See {@link Exception#Exception(String, Throwable)}
	 */
	public ChecksumException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * See {@link Exception#Exception(String)}
	 */
	public ChecksumException(final String message) {
		super(message);
	}

	/**
	 * See {@link Exception#Exception(Throwable)}
	 */
	public ChecksumException(final Throwable cause) {
		super(cause);
	}
}
