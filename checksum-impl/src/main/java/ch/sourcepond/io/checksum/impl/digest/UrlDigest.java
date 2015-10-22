package ch.sourcepond.io.checksum.impl.digest;

import java.io.IOException;
import java.net.URL;

/**
 * @author rolandhauser
 *
 */
final class UrlDigest extends UpdatableDigest<URL> {
	private volatile InputStreamDigester digester;

	UrlDigest(final String pAlgorithm, final URL pSource) {
		super(pAlgorithm, pSource);
	}

	@Override
	public byte[] updateDigest() throws IOException {
		digester = new InputStreamDigester(getAlgorithm(), getSource().openStream());
		return digester.call();
	}

	@Override
	public void cancel() {
		digester.cancel();
	}
}
