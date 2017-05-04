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
package ch.sourcepond.io.checksum.impl.resources;

import org.junit.Test;

import static ch.sourcepond.io.checksum.impl.resources.ResourceNotAvailable.ARR;
import static ch.sourcepond.io.checksum.impl.resources.ResourceNotAvailable.EMPTY;
import static ch.sourcepond.io.checksum.impl.resources.ResourceNotAvailable.RESOURCE_NOT_AVAILABLE;
import static java.time.Instant.MIN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;

/**
 *
 */
public class ResourceNotAvailableTest {

    @Test
    public void verifyEmptyChecksum() {
        assertSame(MIN, EMPTY.getTimestamp());
        assertSame(ARR, EMPTY.toByteArray());
        assertEquals("", EMPTY.getHexValue());
    }

    @Test
    public void verifyFuture() {
        assertTrue(RESOURCE_NOT_AVAILABLE.isDone());
        assertFalse(RESOURCE_NOT_AVAILABLE.isCancelled());
        assertFalse(RESOURCE_NOT_AVAILABLE.cancel(true));
        assertFalse(RESOURCE_NOT_AVAILABLE.cancel(false));
        assertSame(EMPTY, RESOURCE_NOT_AVAILABLE.get());
        assertSame(EMPTY, RESOURCE_NOT_AVAILABLE.get(9L, SECONDS));
    }
}
