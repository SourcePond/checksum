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
public abstract class BaseResourceTest<S, A> {
    final Future<Checksum> checksumFuture = mock(Future.class);
    final Callable<Checksum> updateTask = mock(Callable.class);
    final ExecutorService updateExecutor = mock(ExecutorService.class);
    final DigesterPool digesterPool = mock(DigesterPool.class);
    final Observers<S, A> observers = mock(Observers.class);
    final TaskFactory taskFactory = mock(TaskFactory.class);
    private final CancelObserver<S> cancelObserver = mock(CancelObserver.class);
    private final FailureObserver<S> failureObserver = mock(FailureObserver.class);
    private final SuccessObserver<S> successObserver = mock(SuccessObserver.class);
    S source;
    Resource<S> resource;

    @Before
    public void setup() {
        when(digesterPool.getAlgorithm()).thenReturn(SHA256);
        when(observers.getSource()).thenReturn(source);
        when(updateExecutor.submit(updateTask)).thenReturn(checksumFuture);
    }

    @Test
    public void getSource() {
        assertSame(source, resource.getSource());
    }

    @Test
    public void getAlgorithm() {
        assertSame(SHA256, resource.getAlgorithm());
    }

    @Test
    public void addChancelObserver() {
        assertSame(resource, resource.addCancelObserver(cancelObserver));
        verify(observers).addCancelObserver(cancelObserver);
    }

    @Test
    public void addFailureObserver() {
        assertSame(resource, resource.addFailureObserver(failureObserver));
        verify(observers).addFailureObserver(failureObserver);
    }

    @Test
    public void addSuccessObserver() {
        assertSame(resource, resource.addSuccessObserver(successObserver));
        verify(observers).addSuccessObserver(successObserver);
    }

    @Test
    public void removeChancelObserver() {
        assertSame(resource, resource.removeCancelObserver(cancelObserver));
        verify(observers).removeCancelObserver(cancelObserver);
    }

    @Test
    public void removeFailureObserver() {
        assertSame(resource, resource.removeFailureObserver(failureObserver));
        verify(observers).removeFailureObserver(failureObserver);
    }

    @Test
    public void removeSuccessObserver() {
        assertSame(resource, resource.removeSuccessObserver(successObserver));
        verify(observers).removeSuccessObserver(successObserver);
    }

    public abstract void update();

    public abstract void updateWithInterval();

    public abstract void updateWithIntervalAndUnit();
}
