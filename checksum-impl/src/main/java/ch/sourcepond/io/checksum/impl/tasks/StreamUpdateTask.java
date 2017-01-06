package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.ResourceCallback;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

/**
 * Created by rolandhauser on 06.01.17.
 */
public class StreamUpdateTask extends UpdateTask {
    private final StreamSource streamSource;

    public StreamUpdateTask(final ResourceCallback pResource, final long pInterval, final TimeUnit pUnit, final StreamSource pStreamSource) {
        super(pResource, pInterval, pUnit);
        streamSource = pStreamSource;
    }

    @Override
    void newDigest(final MessageDigest pDigest) throws IOException {
        try (final InputStream in = streamSource.openStream()) {
            final byte[] buffer = new byte[1024];
            int readBytes;

            while (availability.available()) {
                if ((readBytes = availability.readBytes(in.read(buffer))) != -1) {
                    pDigest.update(buffer, 0, readBytes);
                }
            }
        }
    }
}
