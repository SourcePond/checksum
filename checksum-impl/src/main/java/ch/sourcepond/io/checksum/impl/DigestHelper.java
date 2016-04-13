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
package ch.sourcepond.io.checksum.impl;

import static java.lang.Long.MAX_VALUE;
import static java.nio.channels.FileChannel.open;
import static java.nio.file.StandardOpenOption.READ;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.slf4j.Logger;

/**
 * Utility class to perform the actual checksum digest.
 */
final class DigestHelper {
	/**
	 * 
	 */
	static final int DEFAULT_BUFFER_SIZE = 8192;
	private static final Logger LOG = getLogger(DigestHelper.class);

	/**
	 * Performs the the hash calculation upon the data received through the
	 * input-stream specified.
	 * 
	 * @param pDigest
	 *            The message digester, must not be {@code null}
	 * @param pCancellable
	 *            Cancel callback, must not be {@code null}
	 * @param pSource
	 *            Stream where to read the data from, must not be {@code null}
	 * @return The digested hash, never {@code null}
	 * @throws IOException
	 *             Thrown, if an exception occurred while reading the data.
	 */
	public static void performUpdate(final MessageDigest pDigest, final Cancellable pCancellable, final InputStream pSource)
			throws IOException {
		try (final DigestInputStream din = new DigestInputStream(pSource, pDigest)) {
			final byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
			int read = din.read(buf);
			while (!pCancellable.isCancelled() && read != -1) {
				read = din.read(buf);
			}

			if (pCancellable.isCancelled()) {
				LOG.debug("Checksum calculation cancelled by user.");
			}
		}
	}

	/**
	 * Performs the the hash calculation upon the data provided by the path
	 * specified.
	 * 
	 * @param digest
	 *            The message digester, must not be {@code null}
	 * @param pCancellable
	 *            Cancel callback, must not be {@code null}
	 * @param pPath
	 *            The path where to read the data from; must be a regular file
	 *            and must not be {@code null}
	 * @param buffer
	 *            The {@link ByteBuffer} instance where to store read data.
	 * @throws IOException
	 *             Thrown, if an exception occurred while reading the data.
	 */
	public static void performUpdate(final MessageDigest digest, final Cancellable pCancellable, final Path pPath,
			final ByteBuffer buffer) throws IOException {
		try (final FileChannel ch = open(pPath, READ)) {
			final FileLock fl = ch.lock(0l, MAX_VALUE, true);
			try {
				final byte[] tmp = new byte[DEFAULT_BUFFER_SIZE];
				int read = ch.read(buffer);
				while (!pCancellable.isCancelled() && read != -1) {
					buffer.flip();
					buffer.get(tmp, 0, read);
					digest.update(tmp, 0, read);
					buffer.clear();
					read = ch.read(buffer);
				}

				if (pCancellable.isCancelled()) {
					LOG.debug("Checksum calculation cancelled by user.");
				}
			} finally {
				fl.release();
			}
		}
	}
}
