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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Abstract base implementation of the {@link Resource} interface.
 *
 * @param <S> Type of the source which is associated with this resource.
 * @param <A> Type of the accessor necessary to read from the source. This can either be
 *            {@link ChannelSource} or {@link StreamSource}.
 */
abstract class BaseResource<S, A> implements Resource<S> {
    private final ExecutorService updateExecutor;
    final DigesterPool digesterPool;
    final Observers<S, A> observers;
    final TaskFactory taskFactory;

    BaseResource(final ExecutorService pUpdateExecutor,
            final DigesterPool pDigesterPool,
            final Observers<S, A> pObservers,
            final TaskFactory pTaskFactory) {
        updateExecutor = pUpdateExecutor;
        digesterPool = pDigesterPool;
        observers = pObservers;
        taskFactory = pTaskFactory;
    }

    @Override
    public final Algorithm getAlgorithm() {
        return digesterPool.getAlgorithm();
    }

    @Override
    public final S getSource() {
        return observers.getSource();
    }

    @Override
    public final Resource<S> addCancelObserver(final CancelObserver<S> pObserver) {
        observers.addCancelObserver(pObserver);
        return this;
    }

    @Override
    public final Resource<S> addFailureObserver(final FailureObserver<S> pObserver) {
        observers.addFailureObserver(pObserver);
        return this;
    }

    @Override
    public final Resource<S> addSuccessObserver(final SuccessObserver<S> pObserver) {
        observers.addSuccessObserver(pObserver);
        return this;
    }

    @Override
    public final Resource<S> removeCancelObserver(final CancelObserver<S> pObserverOrNull) {
        observers.removeCancelObserver(pObserverOrNull);
        return this;
    }

    @Override
    public final Resource<S> removeFailureObserver(final FailureObserver<S> pObserverOrNull) {
        observers.removeFailureObserver(pObserverOrNull);
        return this;
    }

    @Override
    public final Resource<S> removeSuccessObserver(final SuccessObserver<S> pObserverOrNull) {
        observers.removeSuccessObserver(pObserverOrNull);
        return this;
    }

    @Override
    public final Future<Checksum> update() {
        return update(0L);
    }

    @Override
    public final Future<Checksum> update(final long pIntervalInMilliseconds) {
        return update(MILLISECONDS, pIntervalInMilliseconds);
    }

    @Override
    public final Future<Checksum> update(final TimeUnit pUnit, final long pInterval) {
        return updateExecutor.submit(newUpdateTask(pUnit, pInterval));
    }

    abstract Callable<Checksum> newUpdateTask(TimeUnit pUnit, long pInterval);
}
