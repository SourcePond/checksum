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
package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.Update;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;

import static ch.sourcepond.io.checksum.impl.resources.InitialChecksum.EMPTY;
import static java.time.Instant.MIN;
import static java.time.Instant.now;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InitialChecksumTest {
    private static final Instant EXPECTED_TIMESTAMP = now();
    private static final String EXPECTED_HEX_VALUE = "someHexValue";
    private static final byte[] EXPECTED_BYTES = new byte[10];
    private final Checksum current = mock(Checksum.class);
    private final Update update = mock(Update.class);
    private final InitialChecksum checksum = new InitialChecksum();
    private final ScheduledExecutorService executor = newSingleThreadScheduledExecutor();

    @Before
    public void setup() {
        when(current.getHexValue()).thenReturn(EXPECTED_HEX_VALUE);
        when(current.getTimestamp()).thenReturn(EXPECTED_TIMESTAMP);
        when(current.toByteArray()).thenReturn(EXPECTED_BYTES);
        when(update.getCurrent()).thenReturn(current);
    }

    @After
    public void tearDown() {
        executor.shutdown();
    }

    @Test(timeout = 2000)
    public void awaitCalculation() {
        executor.schedule(() -> {
            checksum.done(update);
        }, 500, MILLISECONDS);
        assertSame(EXPECTED_BYTES, checksum.toByteArray());
        assertSame(EXPECTED_HEX_VALUE, checksum.getHexValue());
        assertSame(EXPECTED_TIMESTAMP, checksum.getTimestamp());
    }

    @Test
    public void verifyDefaults() {
        checksum.initDefaults();
        assertSame("", checksum.getHexValue());
        assertSame(MIN, checksum.getTimestamp());
        assertSame(EMPTY, checksum.toByteArray());
    }
}
