package ch.sourcepond.io.checksum.impl.digest;

import static ch.sourcepond.io.checksum.impl.DefaultChecksumBuilderFactory.DEFAULT_BUFFER_SIZE;
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
 * @author rolandhauser
 *
 */
final class DigestHelper {
	private static final Logger LOG = getLogger(DigestHelper.class);

	/**
	 * @param digest
	 * @param pCancellable
	 * @param pSource
	 * @return
	 * @throws IOException
	 */
	public static byte[] perform(final MessageDigest digest, Cancellable pCancellable, final InputStream pSource)
			throws IOException {
		try (final DigestInputStream din = new DigestInputStream(pSource, digest)) {
			final byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
			int read = din.read(buf);
			while (!pCancellable.isCancelled() && read != -1) {
				read = din.read(buf);
			}

			if (pCancellable.isCancelled()) {
				LOG.info("Checksum calculation cancelled by user.");
				return null;
			}
		}
		return digest.digest();
	}

	/**
	 * @param pChannel
	 * @throws IOException
	 */
	public static void performUpdate(MessageDigest digest, final Cancellable pCancellable, final Path pPath,
			ByteBuffer buffer) throws IOException {
		try (final FileChannel ch = open(pPath, READ)) {
			final FileLock fl = ch.lock(0, MAX_VALUE, true);
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
			} finally {
				fl.release();
			}
		}
	}
}
