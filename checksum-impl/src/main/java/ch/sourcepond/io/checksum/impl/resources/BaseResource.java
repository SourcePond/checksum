package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.*;
import ch.sourcepond.io.checksum.impl.DisposeCallback;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 *
 */
abstract class BaseResource<S, A> implements Resource<S> {
    private final AtomicInteger usages = new AtomicInteger();
    private final DisposeCallback disposeCallback;
    private final ExecutorService updateExecutor;
    final Observers<S, A> observers;
    final TaskFactory taskFactory;

    BaseResource(final DisposeCallback pDisposeCallback,
                 final ExecutorService pUpdateExecutor,
                 final Observers<S, A> pObservers,
                 final TaskFactory pTaskFactory) {
        disposeCallback = pDisposeCallback;
        updateExecutor = pUpdateExecutor;
        observers = pObservers;
        taskFactory = pTaskFactory;
    }

    public final Resource lease() {
        usages.incrementAndGet();
        return this;
    }

    @Override
    public final void release() {
        if (usages.decrementAndGet() == 0) {
            disposeCallback.dispose(this);
        }
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
        return update(pIntervalInMilliseconds, MILLISECONDS);
    }

    @Override
    public final Future<Checksum> update(final long pInterval, final TimeUnit pUnit) {
        return updateExecutor.submit(newUpdateTask(pUnit, pInterval));
    }

    abstract Callable<Checksum> newUpdateTask(TimeUnit pUnit, long pInterval);
}
