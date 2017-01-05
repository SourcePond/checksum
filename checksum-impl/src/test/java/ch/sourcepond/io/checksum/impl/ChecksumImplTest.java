package ch.sourcepond.io.checksum.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Created by rolandhauser on 05.01.17.
 */
public class ChecksumImplTest {
    private static final String EXPECTED_ALGORITHM = "SHA-256";
    private static final String EXPECTED_SHA_256_HASH = "b0a0a864cf2eb7c20a25bfe12f4cddc6070809e5da8f5da226234a258d17d336";
    private byte[] expectedBytes;
    private ChecksumImpl checksum;

    @Before
    public void setup() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (final BufferedInputStream in = new BufferedInputStream(getClass().getResourceAsStream("/testfile_01.txt"))) {
            final byte[] buffer = new byte[1024];
            int readBytes;
            while ((readBytes = in.read(buffer)) != -1) {
                digest.update(buffer, 0, readBytes);
            }
        }
        expectedBytes = digest.digest();
        checksum = new ChecksumImpl(EXPECTED_ALGORITHM, expectedBytes);
    }

    @Test
    public void verifyChecksum() throws Exception {
        assertEquals(EXPECTED_ALGORITHM, checksum.getAlgorithm());
        assertSame(expectedBytes, checksum.getValue());
        assertEquals(EXPECTED_SHA_256_HASH, checksum.getHexValue());
    }
}
