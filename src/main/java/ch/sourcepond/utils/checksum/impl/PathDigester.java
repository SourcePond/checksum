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
package ch.sourcepond.utils.checksum.impl;

import static ch.sourcepond.utils.checksum.impl.DefaultChecksumFactory.DEFAULT_BUFFER_SIZE;
import static java.lang.Long.MAX_VALUE;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.channels.FileChannel.open;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.StandardOpenOption.READ;
import static java.security.MessageDigest.getInstance;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is <em>not</em> thread-safe and must by synchronized externally.
 *
 */
class PathDigester extends SimpleFileVisitor<Path> {
	private final String algorithm;
	private final Path path;

	// To safe as much system resources as possible, we do not hold hard
	// references to the digester/tempBuffer.
	private WeakReference<ByteBuffer> bufferRef;
	private WeakReference<MessageDigest> digestRef;

	// These fields will be initialized when an update is performed. After the
	// update they will be set to null.
	private ByteBuffer tempBuffer;
	private MessageDigest tempDigest;

	/**
	 * @param pBuffers
	 * @param pDigest
	 * @throws NoSuchAlgorithmException
	 */
	PathDigester(final String pAlgorithm, final Path pPath) throws NoSuchAlgorithmException {
		algorithm = pAlgorithm;
		path = pPath;
		bufferRef = new WeakReference<ByteBuffer>(allocateDirect(DEFAULT_BUFFER_SIZE));
		digestRef = new WeakReference<MessageDigest>(getInstance(pAlgorithm));
	}

	/**
	 * @return
	 */
	Path getPath() {
		return path;
	}

	/**
	 * @return
	 */
	String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param pChannel
	 * @throws IOException
	 */
	private void updateDigest(final Path pPath) throws IOException {
		try (final FileChannel ch = open(pPath, READ)) {
			final FileLock fl = ch.lock(0, MAX_VALUE, true);
			try {
				final byte[] tmp = new byte[DEFAULT_BUFFER_SIZE];
				int read = ch.read(tempBuffer);
				while (read != -1) {
					tempBuffer.flip();
					tempBuffer.get(tmp, 0, read);
					tempDigest.update(tmp, 0, read);
					tempBuffer.clear();
					read = ch.read(tempBuffer);
				}
			} finally {
				fl.release();
			}
		}
	}

	/**
	 * @param pChannel
	 * @throws IOException
	 */
	byte[] updateDigest() throws IOException {
		// Initialize the temporary hard reference to the digester; this must be
		// set to null after the update has been performed.
		tempDigest = digestRef.get();
		if (tempDigest == null) {
			try {
				tempDigest = getInstance(getAlgorithm());
			} catch (final NoSuchAlgorithmException e) {
				// This can never happen because it has already been validated
				// during construction that the algorithm is available.
				throw new IllegalStateException(e.getMessage(), e);
			}
			digestRef = new WeakReference<MessageDigest>(tempDigest);
		}

		// Initialize the temporary hard reference to the tempBuffer; this must
		// be set to null after the update has been performed.
		tempBuffer = bufferRef.get();
		if (tempBuffer == null) {
			tempBuffer = allocateDirect(DEFAULT_BUFFER_SIZE);
			bufferRef = new WeakReference<ByteBuffer>(tempBuffer);
		}

		try {
			if (isDirectory(path)) {
				walkFileTree(path, this);
			} else {
				updateDigest(path);
			}

			return tempDigest.digest();
		} finally {
			tempDigest = null;
			tempBuffer = null;
		}
	}

	/**
	 * @param file
	 * @param attrs
	 * @return
	 * @throws IOException
	 */
	@Override
	public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
		updateDigest(file);
		return super.visitFile(file, attrs);
	}
}
