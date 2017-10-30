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

import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;
import ch.sourcepond.io.checksum.impl.tasks.UpdateTask;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class BaseResourceTest<A> {
    final UpdateTask<A> initialUpdateTask = mock(UpdateTask.class);
    final UpdateTask<A> updateTask = mock(UpdateTask.class);
    final ScheduledExecutorService updateExecutor = mock(ScheduledExecutorService.class);
    final DigesterPool digesterPool = mock(DigesterPool.class);
    final TaskFactory taskFactory = mock(TaskFactory.class);
    final UpdateObserver observer = mock(UpdateObserver.class);
    A source;
    BaseResource<A> resource;

    @Before
    public void setup() throws IOException {
        when(digesterPool.getAlgorithm()).thenReturn(SHA256);
    }

    public abstract void updateIOExceptionOccurred() throws IOException;

    public abstract void initialUpdateIOExceptionOccurred() throws IOException;

    @Test
    public void getAlgorithm() {
        assertSame(SHA256, resource.getAlgorithm());
    }

    public abstract void update() throws IOException;

    public abstract void updateWithInterval() throws IOException;

    public abstract void updateWithIntervalAndUnit() throws IOException;
}
