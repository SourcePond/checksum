package ch.sourcepond.io.checksum.impl.digest;

import java.io.IOException;
import java.net.URL;

/**
 * @author rolandhauser
 *
 */
final class UrlDigest extends UpdatableDigest<URL> {

	UrlDigest(final String pAlgorithm, final URL pSource) {
		super(pAlgorithm, pSource);
	}

	@Override
	public byte[] updateDigest() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
