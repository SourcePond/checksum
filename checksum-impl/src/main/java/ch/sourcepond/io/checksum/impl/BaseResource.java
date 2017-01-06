package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.ObservedResource;
import ch.sourcepond.io.checksum.api.FailureObserver;
import ch.sourcepond.io.checksum.api.SuccessObserver;
import ch.sourcepond.io.checksum.impl.tasks.UpdateTask;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;

/**
 * Created by rolandhauser on 05.01.17.
 */
abstract class BaseResource<T> implements ObservedResource<T> {
    private final Queue<UpdateTask<T>> updateRequests = new LinkedBlockingQueue<>();
    private final AtomicInteger usages = new AtomicInteger();
    private final ObservedResourcesRegistryImpl registry;
    private final ExecutorService updateExecutor;

    BaseResource(final ObservedResourcesRegistryImpl pRegistry,
                 final ExecutorService pUpdateExecutor,
                 final MessageDigest pDigest) {
        assert pRegistry != null : "pRegistry is null";
        assert pUpdateExecutor != null : "pUpdateExecutor is null";
        assert pDigest != null : "pDigest is null";
        registry = pRegistry;
        updateExecutor = pUpdateExecutor;
        digest = pDigest;
    }

    ObservedResource lease() {
        usages.incrementAndGet();
        return this;
    }

    @Override
    public void dispose() {
        if (usages.decrementAndGet() == 0) {
            registry.remove(this);
        }
    }

    @Override
    public ObservedResource<T> addUpdateObserver(final UpdateObserver pObserver) {
        observers.add(pObserver);
        return this;
    }

    @Override
    public ObservedResource<T> removeUpdateObserver(final UpdateObserver pObserverOrNull) {
        observers.remove(pObserverOrNull);
        return this;
    }

    @Override
    public ObservedResource<T> cancel() {
        cancelled.set(true);
        return this;
    }

    @Override
    public ObservedResource<T> update() {
        return null;
    }

    @Override
    public ObservedResource<T> update(final long pIntervalInMilliseconds) {
        return null;
    }

    private <S> Collection<S> getObservers(Class<S> pType) {
        return observers.stream().filter(
                o -> pType.isAssignableFrom(o.getClass())).map(
                o -> (S) o).collect(Collectors.toList());
    }

    protected abstract T getSource();

    protected void informSuccessObservers(final T pSource) {
        getObservers(SuccessObserver.class).forEach(o -> {

        });
    }

    protected void informFailureObservers(final IOException e) {
        getObservers(FailureObserver.class).forEach(o -> {
            o.updateFailed(getSource(), e);
        });
    }

    abstract void doUpdate(final long pInterval, final TimeUnit pUnit) throws IOException;

    @Override
    public ObservedResource update(final long pInterval, final TimeUnit pUnit) {


        updateExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    doUpdate(pInterval, pUnit);
                } catch (IOException e) {
                    informFailureObservers(e);
                }

            }
        });
        return this;
    }
}
