package ch.sourcepond.io.checksum.impl.digest;

import java.io.IOException;
import java.net.URL;

/**
 * @author rolandhauser
 *
 */
final class UrlDigest extends UpdatableDigest<URL> {
	private final InputStreamDigester digester;

	UrlDigest(final String pAlgorithm, final URL pSource, final InputStreamDigester pDigester) {
		super(pAlgorithm, pSource);
		digester = pDigester;
	}

	@Override
	public byte[] updateDigest() throws IOException {
		return digester.digest(getSource().openStream());
	}

	@Override
	public void cancel() {
		digester.cancel();
	}
}
