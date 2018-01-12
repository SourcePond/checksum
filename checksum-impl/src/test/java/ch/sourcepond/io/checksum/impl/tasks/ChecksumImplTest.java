package ch.sourcepond.io.checksum.impl.tasks;

import org.junit.Test;

import java.time.Instant;

import static java.time.Instant.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ChecksumImplTest {
    private static final String EXPECTED_SHA_256_HASH = "536f6d65207465787420746f206265206469676573746564";
    private static final byte[] EXPECTED_BYTES = "Some text to be digested".getBytes();
    private final Instant timestamp = now();
    private ChecksumImpl checksum = new ChecksumImpl(timestamp, EXPECTED_BYTES);

    @Test
    public void verifyChecksum() throws Exception {
        assertSame(EXPECTED_BYTES, checksum.toByteArray());
        assertEquals(EXPECTED_SHA_256_HASH, checksum.getHexValue());
        assertSame(timestamp, checksum.getTimestamp());
    }

    @SuppressWarnings({"ObjectEqualsNull", "EqualsWithItself"})
    @Test
    public void verifyEquals() {
        assertTrue(checksum.equals(checksum));
        assertFalse(checksum.equals(null));
        assertFalse(checksum.equals(new Object()));

        final ChecksumImpl second = new ChecksumImpl(now(), checksum.toByteArray());
        assertTrue(checksum.equals(second));
        assertEquals(checksum.hashCode(), second.hashCode());
    }

    @Test
    public void verifyToString() {
        assertEquals("ChecksumImpl[hexValue: 536f6d65207465787420746f206265206469676573746564, " +
                "timestamp: " + timestamp.toEpochMilli() + "]", checksum.toString());
    }
}
