package ch.sourcepond.io.checksum.impl.tasks;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.security.MessageDigest;
import java.time.Instant;

import static java.time.Instant.now;
import static org.junit.Assert.*;

/**
 *
 */
public class ChecksumImplTest {
    private static final String EXPECTED_SHA_256_HASH = "b0a0a864cf2eb7c20a25bfe12f4cddc6070809e5da8f5da226234a258d17d336";
    private final Instant timestamp = now();
    private byte[] expectedBytes;
    private ChecksumImpl checksum;

    @Before
    public void setup() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (final InputStream in = getClass().getResourceAsStream("/testfile_01.txt")) {
            final byte[] buffer = new byte[1024];
            int readBytes;
            while ((readBytes = in.read(buffer)) != -1) {
                digest.update(buffer, 0, readBytes);
            }
        }
        expectedBytes = digest.digest();
        checksum = new ChecksumImpl(timestamp, expectedBytes);
    }

    @Test
    public void verifyChecksum() throws Exception {
        assertSame(expectedBytes, checksum.getValue());
        assertEquals(EXPECTED_SHA_256_HASH, checksum.getHexValue());
        assertSame(timestamp, checksum.getTimestamp());
    }

    @SuppressWarnings({"ObjectEqualsNull", "EqualsWithItself"})
    @Test
    public void verifyEquals() {
        assertTrue(checksum.equals(checksum));
        assertFalse(checksum.equals(null));
        assertFalse(checksum.equals(new Object()));

        final ChecksumImpl second = new ChecksumImpl(now(), checksum.getValue());
        assertTrue(checksum.equals(second));
        assertEquals(checksum.hashCode(), second.hashCode());
    }
}
