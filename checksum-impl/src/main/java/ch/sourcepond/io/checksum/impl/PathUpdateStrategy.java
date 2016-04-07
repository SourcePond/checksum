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

import static ch.sourcepond.io.checksum.impl.DigestHelper.DEFAULT_BUFFER_SIZE;
import static ch.sourcepond.io.checksum.impl.DigestHelper.performUpdate;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.file.FileVisitResult.TERMINATE;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.walkFileTree;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is <em>not</em> thread-safe and must by synchronized externally
 * (except of {@link #cancel()}).
 *
 */
class PathUpdateStrategy extends UpdateStrategy<Path> {
	/**
	 * Visitor to scan a directory structure for files to be digested.
	 */
	private final FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
		/**
		 * @param file
		 * @param attrs
		 * @return
		 * @throws IOException
		 */
		@Override
		public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
			if (!isCancelled()) {
				performUpdate(tempDigest, PathUpdateStrategy.this, file, tempBuffer);
				return super.visitFile(file, attrs);
			}
			return TERMINATE;
		}
	};

	// To safe as much system resources as possible, we do not hold hard
	// references to the tempBuffer.
	private WeakReference<ByteBuffer> bufferRef;

	// These fields will be initialized when an update is performed. After the
	// update they will be set to null.
	private ByteBuffer tempBuffer;
	private MessageDigest tempDigest;

	/**
	 * @param pBuffers
	 * @param pDigest
	 * @throws NoSuchAlgorithmException
	 */
	PathUpdateStrategy(final String pAlgorithm, final Path pPath) {
		super(pAlgorithm, pPath);
		bufferRef = new WeakReference<ByteBuffer>(allocateDirect(DEFAULT_BUFFER_SIZE));
	}

	/**
	 * @param pChannel
	 * @throws IOException
	 */
	@Override
	protected byte[] doUpdate() throws IOException {
		// Initialize the temporary hard reference to the digester; this must be
		// set to null after the update has been performed.
		tempDigest = getDigest();

		// Initialize the temporary hard reference to the tempBuffer; this must
		// be set to null after the update has been performed.
		tempBuffer = bufferRef.get();
		if (tempBuffer == null) {
			tempBuffer = allocateDirect(DEFAULT_BUFFER_SIZE);
			bufferRef = new WeakReference<ByteBuffer>(tempBuffer);
		}

		try {
			if (isDirectory(getSource())) {
				walkFileTree(getSource(), visitor);
			} else {
				performUpdate(tempDigest, this, getSource(), tempBuffer);
			}

			byte[] res = null;
			if (!isCancelled()) {
				res = tempDigest.digest();
			}
			return res;
		} finally {
			tempDigest = null;
			tempBuffer = null;
		}
	}
}
