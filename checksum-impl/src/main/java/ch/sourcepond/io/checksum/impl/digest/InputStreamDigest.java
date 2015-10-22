package ch.sourcepond.io.checksum.impl.digest;

import static ch.sourcepond.io.checksum.impl.digest.DigestHelper.perform;
import static java.security.MessageDigest.getInstance;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;

/**
 * @author rolandhauser
 *
 */
public class InputStreamDigest extends Digest<InputStream> implements Callable<byte[]> {

	/**
	 * @param pAlgorithm
	 * @param pSource
	 */
	InputStreamDigest(final String pAlgorithm, final InputStream pSource) {
		super(pAlgorithm, pSource);
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
			return perform(digest, this, getSource());
		} catch (final NoSuchAlgorithmException e) {
			// This should never happen because it has been verified that the
			// algorithm exists during construction of this builder.
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
