package ch.sourcepond.io.checksum.impl.digest;

import static ch.sourcepond.io.checksum.impl.DefaultChecksumBuilderFactory.DEFAULT_BUFFER_SIZE;
import static java.security.MessageDigest.getInstance;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

/**
 * @author rolandhauser
 *
 */
public class InputStreamDigester extends Digest<InputStream> implements Callable<byte[]> {
	private static final Logger LOG = getLogger(InputStreamDigester.class);
	private volatile boolean cancelled;

	/**
	 * @param pAlgorithm
	 * @param pSource
	 */
	InputStreamDigester(final String pAlgorithm, final InputStream pSource) {
		super(pAlgorithm, pSource);
	}

	/**
	 * 
	 */
	@Override
	public void cancel() {
		cancelled = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public byte[] call() throws IOException {
		try {
			final MessageDigest digest = getInstance(getAlgorithm());
			try (final DigestInputStream din = new DigestInputStream(getSource(), digest)) {
				final byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
				int read = din.read(buf);
				while (!cancelled && read != -1) {
					read = din.read(buf);
				}

				if (cancelled) {
					LOG.info("Checksum calculation cancelled by user.");
					return null;
				}
			}
			return digest.digest();
		} catch (final NoSuchAlgorithmException e) {
			// This should never happen because it has been verified that the
			// algorithm exists during construction of this builder.
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
