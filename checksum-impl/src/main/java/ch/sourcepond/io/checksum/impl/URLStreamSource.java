package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.StreamSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by rolandhauser on 05.01.17.
 */
final class URLStreamSource implements StreamSource {
    private final URL url;

    public URLStreamSource(final URL url) {
        this.url = url;
    }

    @Override
    public InputStream openStream() throws IOException {
        return url.openStream();
    }
}
