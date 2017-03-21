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
package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class UpdateImplTest {
    private final Checksum previous = mock(Checksum.class);
    private final Checksum current = mock(Checksum.class);
    private final Exception failure = new Exception();
    private UpdateImpl update = new UpdateImpl(previous, current, null);

    @Test
    public void getPrevious() {
        assertSame(previous, update.getPrevious());
    }

    @Test
    public void getCurrent() {
        assertSame(current, update.getCurrent());
    }

    @Test
    public void getFailureOrNull() {
        assertNull(update.getFailureOrNull());
        update = new UpdateImpl(previous, current, failure);
        assertSame(failure, update.getFailureOrNull());
    }

    @Test
    public void hasChanged() {
        update = new UpdateImpl(previous, previous, null);
        assertFalse(update.hasChanged());
        update = new UpdateImpl(previous, current, null);
        assertTrue(update.hasChanged());
        update = new UpdateImpl(previous, current, failure);
        assertFalse(update.hasChanged());
    }
}
