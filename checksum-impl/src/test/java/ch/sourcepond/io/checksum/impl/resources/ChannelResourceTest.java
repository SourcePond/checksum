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

import ch.sourcepond.io.checksum.api.ChannelSource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static ch.sourcepond.io.checksum.impl.resources.ResourceNotAvailable.RESOURCE_NOT_AVAILABLE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ChannelResourceTest extends BaseResourceTest<ChannelSource> {

    @Before
    @Override
    public void setup() throws IOException {
        source = mock(ChannelSource.class);
        resource = new ChannelResource(updateExecutor, source, digesterPool, taskFactory);
        when(taskFactory.newChannelTask(notNull(), same(digesterPool), same(resource), eq(MILLISECONDS), eq(0L))).thenReturn(initialUpdateTask);
        resource.initialUpdate();
        super.setup();
    }

    @Test
    @Override
    public void updateIOExceptionOccurred() throws IOException {
        doThrow(IOException.class).when(taskFactory).newChannelTask(observer, digesterPool, resource, MILLISECONDS, 0L);
        assertSame(RESOURCE_NOT_AVAILABLE, resource.update(observer));
    }

    @Test
    @Override
    public void update() throws IOException {
        when(taskFactory.newChannelTask(observer, digesterPool, resource, MILLISECONDS, 0L)).thenReturn(updateTask);
        assertSame(future, resource.update(observer));
    }

    @Test
    @Override
    public void updateWithInterval() throws IOException {
        when(taskFactory.newChannelTask(observer, digesterPool, resource, MILLISECONDS, 100L)).thenReturn(updateTask);
        assertSame(future, resource.update(100L, observer));
    }

    @Test
    @Override
    public void updateWithIntervalAndUnit() throws IOException {
        when(taskFactory.newChannelTask(observer, digesterPool, resource, SECONDS, 200L)).thenReturn(updateTask);
        assertSame(future, resource.update(SECONDS, 200L, observer));
    }
}
