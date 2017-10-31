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
import ch.sourcepond.io.checksum.api.StreamSource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static java.time.Instant.MIN;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 *
 */
public class StreamResourceTest extends BaseResourceTest<StreamSource> {

    @Before
    @Override
    public void setup() throws IOException {
        resource = new StreamResource(updateExecutor, source, digesterPool, taskFactory);
        when(taskFactory.newStreamTask(notNull(), same(digesterPool), same(resource), eq(MILLISECONDS), eq(0L))).thenReturn(initialUpdateTask);
        super.setup();
    }

    @Test(expected = IOException.class)
    @Override
    public void updateIOExceptionOccurred() throws IOException {
        doThrow(IOException.class).when(taskFactory).newStreamTask(observer, digesterPool, resource, MILLISECONDS, 0L);
        resource.update(observer);
    }

    @Test(timeout = 1000)
    @Override
    public void initialUpdateIOExceptionOccurred() throws IOException {
        final IOException expected = new IOException();
        doThrow(expected).when(taskFactory).newStreamTask(argThat(u -> u instanceof InitialChecksum),
                same(digesterPool), same(resource), same(MILLISECONDS), eq(0L));
        assertSame(resource, resource.initialUpdate());
        final Checksum checksum = resource.getCurrent();
        assertSame("", checksum.getHexValue());
        assertSame(MIN, checksum.getTimestamp());
        assertArrayEquals(new byte[0], checksum.toByteArray());
    }

    @Test
    @Override
    public void update() throws IOException {
        when(taskFactory.newStreamTask(observer, digesterPool, resource, MILLISECONDS, 0L)).thenReturn(updateTask);
        resource.update(observer);
    }

    @Test
    @Override
    public void updateWithInterval() throws IOException {
        when(taskFactory.newStreamTask(observer, digesterPool, resource, MILLISECONDS, 100L)).thenReturn(updateTask);
        resource.update(100L, observer);
    }

    @Test
    @Override
    public void updateWithIntervalAndUnit() throws IOException {
        when(taskFactory.newStreamTask(observer, digesterPool, resource, SECONDS, 200L)).thenReturn(updateTask);
        resource.update(SECONDS, 200L, observer);
    }
}
