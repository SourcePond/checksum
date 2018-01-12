/*Copyright (C) 2015 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.io.checksum.api;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.security.MessageDigest;

import static org.junit.Assert.assertEquals;

public class ChecksumTest {
    private static final String EXPECTED_SHA_256_HASH = "b0a0a864cf2eb7c20a25bfe12f4cddc6070809e5da8f5da226234a258d17d336";
    private byte[] expectedBytes;

    @Before
    public void setup() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (final InputStream in = getClass().getResourceAsStream("/test.txt")) {
            final byte[] buffer = new byte[1024];
            int readBytes;
            while ((readBytes = in.read(buffer)) != -1) {
                digest.update(buffer, 0, readBytes);
            }
        }
        expectedBytes = digest.digest();
    }

    @Test
    public void toHexString() {
        assertEquals(EXPECTED_SHA_256_HASH, Checksum.toHexString(expectedBytes));
    }
}
