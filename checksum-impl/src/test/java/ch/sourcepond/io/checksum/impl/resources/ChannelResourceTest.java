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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ChannelResourceTest extends BaseResourceTest<ChannelSource, ChannelSource> {

    @Before
    @Override
    public void setup() {
        resource = new ChannelResource<>(disposeCallback, updateExecutor, digesterPool, observers, taskFactory);
        source = mock(ChannelSource.class);
        super.setup();
    }

    @Test
    @Override
    public void update() {
        when(taskFactory.newChannelTask(digesterPool, observers, MILLISECONDS, 0L)).thenReturn(updateTask);
        assertSame(checksumFuture, resource.update());
    }

    @Test
    @Override
    public void updateWithInterval() {
        when(taskFactory.newChannelTask(digesterPool, observers, MILLISECONDS, 100L)).thenReturn(updateTask);
        assertSame(checksumFuture, resource.update(100L));
    }

    @Test
    @Override
    public void updateWithIntervalAndUnit() {
        when(taskFactory.newChannelTask(digesterPool, observers, SECONDS, 200L)).thenReturn(updateTask);
        assertSame(checksumFuture, resource.update(SECONDS, 200L));
    }
}
