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

import ch.sourcepond.io.checksum.api.Checksum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static ch.sourcepond.io.checksum.impl.resources.ResourceNotAvailable.EMPTY;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.interrupted;
import static java.time.Instant.MAX;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
public class InitialChecksumTest {
    private static final String HEX_VALUE = "hexValue";
    private static final byte[] ARR = new byte[0];
    private final Checksum delegate = mock(Checksum.class);
    private final Future<Checksum> future = mock(Future.class);
    private final InitialChecksum initialChecksum = new InitialChecksum(future);

    @Before
    public void setup() throws Exception {
        when(future.get()).thenReturn(delegate);
        when(delegate.getHexValue()).thenReturn(HEX_VALUE);
        when(delegate.getTimestamp()).thenReturn(MAX);
        when(delegate.toByteArray()).thenReturn(ARR);
    }

    @After
    public void tearDown() {
        interrupted();
    }

    @Test
    public void getTimestamp() {
        assertEquals(MAX, initialChecksum.getTimestamp());
    }

    @Test
    public void toByteArray() {
        assertSame(ARR, initialChecksum.toByteArray());
    }

    @Test
    public void getHexValue() {
        assertEquals(HEX_VALUE, initialChecksum.getHexValue());
    }

    @Test
    public void getHexValueExecutionExeceptionOccurred() throws Exception {
        doThrow(ExecutionException.class).when(future).get();
        assertEquals(EMPTY.getHexValue(), initialChecksum.getHexValue());
        assertFalse(currentThread().isInterrupted());
    }

    @Test
    public void getTimestampExeceptionOccurred() throws Exception {
        doThrow(ExecutionException.class).when(future).get();
        assertEquals(EMPTY.getTimestamp(), initialChecksum.getTimestamp());
        assertFalse(currentThread().isInterrupted());
    }

    @Test
    public void toByteArrayExeceptionOccurred() throws Exception {
        doThrow(ExecutionException.class).when(future).get();
        assertEquals(EMPTY.toByteArray(), initialChecksum.toByteArray());
        assertFalse(currentThread().isInterrupted());
    }

    @Test
    public void getHexValueInterrupted() throws Exception {
        doThrow(InterruptedException.class).when(future).get();
        assertEquals(EMPTY.getHexValue(), initialChecksum.getHexValue());
        assertTrue(currentThread().isInterrupted());
    }

    @Test
    public void getTimestampInterrupted() throws Exception {
        doThrow(InterruptedException.class).when(future).get();
        assertEquals(EMPTY.getTimestamp(), initialChecksum.getTimestamp());
        assertTrue(currentThread().isInterrupted());
    }

    @Test
    public void toByteArrayInterrupted() throws Exception {
        doThrow(InterruptedException.class).when(future).get();
        assertEquals(EMPTY.toByteArray(), initialChecksum.toByteArray());
        assertTrue(currentThread().isInterrupted());
    }
}
