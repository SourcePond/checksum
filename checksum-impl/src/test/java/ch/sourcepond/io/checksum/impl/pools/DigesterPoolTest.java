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
package ch.sourcepond.io.checksum.impl.pools;

import org.junit.Test;

import java.security.MessageDigest;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static org.junit.Assert.*;

/**
 *
 */
public class DigesterPoolTest extends BasePoolTest<MessageDigest> {
    private final byte[] EXPECTED_CLEAN_BYTES = new byte[]{
            -29, -80, -60, 66, -104, -4, 28, 20,
            -102, -5, -12, -56, -103, 111, -71, 36,
            39, -82, 65, -28, 100, -101, -109, 76,
            -92, -107, -103, 27, 120, 82, -72, 85
    };
    private final DigesterPoolRegistry registry = new DigesterPoolRegistry();

    @Override
    protected DigesterPool newTestPool() {
        return registry.get(SHA256);
    }

    @Test
    public void resetDigestAfterRelease() {
        MessageDigest digest = pool.get();

        // Update digest without actually digest the data
        digest.update(new byte[]{2, 3, 4, 5, 6});
        pool.release(digest);
        assertArrayEquals(EXPECTED_CLEAN_BYTES, digest.digest());
    }
}
