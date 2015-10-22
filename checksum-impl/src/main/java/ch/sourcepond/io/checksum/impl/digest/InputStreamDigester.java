package ch.sourcepond.io.checksum.impl.digest;

import static ch.sourcepond.io.checksum.impl.DefaultChecksumBuilderFactory.DEFAULT_BUFFER_SIZE;
import static java.security.MessageDigest.getInstance;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;

/**
 * @author rolandhauser
 *
 */
class InputStreamDigester implements ImmutableDigest {
	private static final Logger LOG = getLogger(InputStreamDigester.class);
	private final InputStream source;
	private final String algorithm;
	private volatile boolean cancelled;

	/**
	 * @param pSource
	 * @param pAlgorithm
	 */
	InputStreamDigester(final InputStream pSource, final String pAlgorithm) {
		source = pSource;
		algorithm = pAlgorithm;
	}

	/**
	 * 
	 */
	@Override
	public void cancel() {
		cancelled = true;
	}

	@Override
	public byte[] call() throws Exception {
		try {
			final MessageDigest digest = getInstance(algorithm);
			try (final DigestInputStream din = new DigestInputStream(source, digest)) {
				final byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
				int read = din.read(buf);
				while (!cancelled && read != -1) {
					read = din.read(buf);
				}

				if (cancelled) {
					if (LOG.isInfoEnabled()) {
						LOG.info("Checksum calculation cancelled by user.");
					}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.digest.Digest#getAlgorithm()
	 */
	@Override
	public String getAlgorithm() {
		return algorithm;
	}

}