package ch.sourcepond.io.checksum.impl.digest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ch.sourcepond.io.checksum.api.StreamSource;

/**
 * @author rolandhauser
 *
 */
class UrlStreamSource implements StreamSource {
	private final URL url;

	UrlStreamSource(final URL pUrl) {
		url = pUrl;
	}

	@Override
	public InputStream openStream() throws IOException {
		return url.openStream();
	}

}
