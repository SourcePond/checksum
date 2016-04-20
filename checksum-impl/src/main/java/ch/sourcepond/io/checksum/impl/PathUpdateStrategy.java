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
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.channels.FileChannel.open;
import static java.nio.file.FileVisitResult.TERMINATE;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is <em>not</em> thread-safe and must by synchronized externally
 * (except of {@link #cancel()}).
 *
 */
class PathUpdateStrategy extends BaseUpdateStrategy<Path> {
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
				performUpdate(file, 0l, MILLISECONDS);
				return super.visitFile(file, attrs);
			}
			return TERMINATE;
		}
	};

	private static final Logger LOG = LoggerFactory.getLogger(PathUpdateStrategy.class);

	// To safe as much system resources as possible, we do not hold hard
	// references to the tempBuffer.
	private WeakReference<ByteBuffer> bufferRef;

	// This field will be initialized when an update is performed. After the
	// update it will be reset to null.
	private ByteBuffer tempBuffer;

	/**
	 * @param pBuffers
	 * @param pDigest
	 * @throws NoSuchAlgorithmException
	 */
	PathUpdateStrategy(final String pAlgorithm, final Path pPath) throws NoSuchAlgorithmException {
		super(pAlgorithm, pPath);
		bufferRef = new WeakReference<ByteBuffer>(allocateDirect(DEFAULT_BUFFER_SIZE));
	}

	private void performUpdate(final Path pFile, final long pInterval, final TimeUnit pUnit) throws IOException {
		try (final FileChannel ch = open(pFile, READ)) {
			final FileLock fl = ch.lock(0l, MAX_VALUE, true);
			try {
				int read = ch.read(tempBuffer);
				while (!isCancelled()) {
					if (read == EOF) {
						wait(pInterval, pUnit);
						read = ch.read(tempBuffer);
						if (read == EOF) {
							break;
						}
					}

					tempBuffer.flip();
					getDigest().update(tempBuffer);
					tempBuffer.clear();
					read = ch.read(tempBuffer);
				}

				if (isCancelled()) {
					LOG.info("Checksum calculation cancelled by user.");
				}
			} finally {
				fl.release();
			}
		}
	}

	protected ByteBuffer getTempBuffer() {
		// Initialize the temporary hard reference to the tempBuffer; this must
		// be set to null after the update has been performed.
		tempBuffer = bufferRef.get();
		if (tempBuffer == null) {
			tempBuffer = allocateDirect(DEFAULT_BUFFER_SIZE);
			bufferRef = new WeakReference<ByteBuffer>(tempBuffer);
		}
		return tempBuffer;
	}

	/**
	 * @param pChannel
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	protected void doUpdate(final long pInterval, final TimeUnit pUnit) throws IOException {
		tempBuffer = getTempBuffer();

		try {
			if (isDirectory(getSource())) {
				walkFileTree(getSource(), visitor);
			} else {
				performUpdate(getSource(), pInterval, pUnit);
			}
		} finally {
			tempBuffer = null;
		}
	}
}
