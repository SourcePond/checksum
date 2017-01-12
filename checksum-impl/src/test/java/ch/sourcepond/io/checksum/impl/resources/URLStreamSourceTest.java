package ch.sourcepond.io.checksum.impl.resources;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by rolandhauser on 11.01.17.
 */
public class URLStreamSourceTest {
    private URLStreamSource source;

    @Before
    public void setup() {
        source = new URLStreamSource(getClass().getResource("/testfile_01.txt"));
    }

    @Test
    public void openStream() throws Exception {
        try (final InputStream in = source.openStream()) {
            // noop
        }
    }
}
