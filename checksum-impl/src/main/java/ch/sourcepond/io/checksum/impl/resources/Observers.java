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

import ch.sourcepond.io.checksum.api.CancelObserver;
import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.FailureObserver;
import ch.sourcepond.io.checksum.api.SuccessObserver;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.requireNonNull;

/**
 * Helper class to manage observers, and, to inform them if something happens.
 */
class Observers<S, A> implements Observable<S, A> {

    /**
     *
     */
    static final Checksum INITIAL_CHECKSUM = new Checksum() {
        private final byte[] value = new byte[0];

        @Override
        public Instant getTimestamp() {
            return Instant.MIN;
        }

        @Override
        public byte[] getValue() {
            return value;
        }

        @Override
        public String getHexValue() {
            return "";
        }
    };

    private final Set<CancelObserver<S>> cancelObservers = new LinkedHashSet<>();
    private final Set<FailureObserver<S>> failureObservers = new LinkedHashSet<>();
    private final Set<SuccessObserver<S>> successObservers = new LinkedHashSet<>();
    private final ExecutorService observerExecutor;
    private final S source;
    private final A accessor;
    private Checksum currentChecksum = INITIAL_CHECKSUM;

    Observers(final ExecutorService pObserverExecutor, final S pSource, final A pAccessor) {
        observerExecutor = pObserverExecutor;
        source = pSource;
        accessor = pAccessor;
    }

    @Override
    public S getSource() {
        return source;
    }

    @Override
    public A getAccessor() {
        return accessor;
    }

    void addCancelObserver(final CancelObserver<S> pObserver) {
        synchronized (cancelObservers) {
            cancelObservers.add(requireNonNull(pObserver, "CancelObserver is null"));
        }
    }

    void addFailureObserver(final FailureObserver<S> pObserver) {
        synchronized (failureObservers) {
            failureObservers.add(requireNonNull(pObserver, "FailureObserver is null"));
        }
    }

    void addSuccessObserver(final SuccessObserver<S> pObserver) {
        synchronized (successObservers) {
            successObservers.add(requireNonNull(pObserver, "SuccessObserver"));
        }
    }

    void removeCancelObserver(final CancelObserver<S> pObserverOrNull) {
        synchronized (cancelObservers) {
            cancelObservers.remove(pObserverOrNull);
        }
    }

    void removeFailureObserver(final FailureObserver<S> pObserverOrNull) {
        synchronized (failureObservers) {
            failureObservers.remove(pObserverOrNull);
        }
    }

    void removeSuccessObserver(final SuccessObserver<S> pObserverOrNull) {
        synchronized (successObservers) {
            successObservers.remove(pObserverOrNull);
        }
    }

    @Override
    public void informCancelObservers() {
        synchronized (cancelObservers) {
            cancelObservers.forEach(observer -> observerExecutor.execute(() -> observer.updateCancelled(source)));
        }
    }

    @Override
    public void informSuccessObservers(final Checksum pChecksum) {
        synchronized (successObservers) {
            final Checksum previous = currentChecksum;
            currentChecksum = pChecksum;
            successObservers.forEach(observer -> observerExecutor.execute(() -> observer.updateSucceeded(source, previous, pChecksum)));
        }
    }

    @Override
    public void informFailureObservers(final IOException e) {
        synchronized (failureObservers) {
            failureObservers.forEach(observer -> observerExecutor.execute(() -> observer.updateFailed(source, e)));
        }
    }
}
