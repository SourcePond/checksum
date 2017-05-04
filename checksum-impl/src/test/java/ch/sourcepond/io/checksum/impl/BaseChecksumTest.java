/*Copyright (C) 2017 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.Checksum;
import org.junit.Test;

import java.time.Instant;

import static java.time.Instant.now;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class BaseChecksumTest {
    private static final Instant NOW = now();
    private static final byte[] ARR = new byte[0];
    private static final String HEX_VALUE = "hexValue";

    private class TestChecksum extends BaseChecksum {

        @Override
        public Instant getTimestamp() {
            return NOW;
        }

        @Override
        public byte[] toByteArray() {
            return ARR;
        }

        @Override
        public String getHexValue() {
            return HEX_VALUE;
        }
    }

    private final TestChecksum checksum = new TestChecksum();

    @Test
    public void verifyEquals() {
        assertFalse(checksum.equals(null));
        assertTrue(checksum.equals(checksum));

        final Checksum other = mock(Checksum.class);
        when(other.getHexValue()).thenReturn(HEX_VALUE);
        assertTrue(checksum.equals(other));

        when(other.getHexValue()).thenReturn("different");
        assertFalse(checksum.equals(other));
    }

    @Test
    public void verifyHashCode() {
        assertEquals(HEX_VALUE.hashCode(), checksum.hashCode());
    }

    @Test
    public void verifyToString() {
        assertEquals("TestChecksum[hexValue: hexValue, " +
                "timestamp: " + NOW.toEpochMilli() + "]", checksum.toString());
    }
}
