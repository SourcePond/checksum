package ch.sourcepond.io.checksum.impl.digest;

import static ch.sourcepond.io.checksum.impl.digest.DigestHelper.perform;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * @author rolandhauser
 *
 */
final class UrlDigest extends UpdatableDigest<URL> {

	/**
	 * @param pAlgorithm
	 * @param pSource
	 * @throws NoSuchAlgorithmException
	 */
	UrlDigest(final String pAlgorithm, final URL pSource) throws NoSuchAlgorithmException {
		super(pAlgorithm, pSource);
	}

	/**
	 * @return
	 * @throws IOException
	 */
	@Override
	public byte[] updateDigest() throws IOException {
		try {
			return perform(getDigest(), this, getSource().openStream());
		} finally {
			setCancelled(false);
		}
	}
}
