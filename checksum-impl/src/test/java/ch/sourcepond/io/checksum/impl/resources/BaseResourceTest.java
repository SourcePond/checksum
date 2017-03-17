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

import ch.sourcepond.io.checksum.api.*;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class BaseResourceTest<A> {
    final Future<Checksum> checksumFuture = mock(Future.class);
    final Callable<Checksum> updateTask = mock(Callable.class);
    final ExecutorService updateExecutor = mock(ExecutorService.class);
    final DigesterPool digesterPool = mock(DigesterPool.class);
    final TaskFactory taskFactory = mock(TaskFactory.class);
    final UpdateObserver observer = mock(UpdateObserver.class);
    A source;
    BaseResource<A> resource;

    @Before
    public void setup() {
        when(digesterPool.getAlgorithm()).thenReturn(SHA256);
        when(updateExecutor.submit(updateTask)).thenReturn(checksumFuture);
    }

    @Test
    public void getAlgorithm() {
        assertSame(SHA256, resource.getAlgorithm());
    }

    public abstract void update();

    public abstract void updateWithInterval();

    public abstract void updateWithIntervalAndUnit();
}
