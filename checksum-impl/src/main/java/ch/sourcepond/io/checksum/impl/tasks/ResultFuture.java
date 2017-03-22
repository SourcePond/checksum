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
package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.Update;
import ch.sourcepond.io.checksum.api.UpdateObserver;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.String.format;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Represents the future result of a checksum update. Until a result is determined,
 * an instance of this class will be accessed from multiple consecutive threads.
 */
public class ResultFuture implements UpdateObserver, Future<Checksum> {
    private final Lock lock = new ReentrantLock();
    private final Condition resultAvailable = lock.newCondition();
    private final UpdateObserver delegate;
    private volatile Checksum result;
    private volatile ExecutionException exception;
    private volatile Thread executingThread;
    private boolean cancelled;

    ResultFuture(final UpdateObserver pDelegate) {
        delegate = pDelegate;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        lock.lock();
        try {
            if (result == null && !cancelled) {
                if (mayInterruptIfRunning && executingThread != null) {
                    executingThread.interrupt();
                }
                cancelled = true;
                return true;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    @Override
    public boolean isCancelled() {
        lock.lock();
        try {
            return cancelled;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isDone() {
        lock.lock();
        try {
            return result != null || exception != null || cancelled;
        } finally {
            lock.unlock();
        }
    }

    private void checkException() throws ExecutionException {
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public Checksum get() throws InterruptedException, ExecutionException {
        checkException();
        if (result == null) {
            lock.lock();
            try {
                while (result == null && exception == null) {
                    resultAvailable.await();
                }

                checkException();
            } finally {
                lock.unlock();
            }
        }
        return result;
    }

    private void waitForResult(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Instant now = now();
        final Instant end = now.plusNanos(unit.toNanos(timeout));

        boolean withinTime = now.isBefore(end);
        while (result == null && exception == null && (withinTime = now.isBefore(end))) {
            resultAvailable.await(now.until(end, NANOS), NANOSECONDS);
            now = now();
        }

        checkException();
        if (!withinTime) {
            throw new TimeoutException(format("Timeout after %d %s", timeout, unit));
        }
    }

    @Override
    public Checksum get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        checkException();
        if (result == null) {
            lock.lock();
            try {
                if (result == null) {
                    waitForResult(timeout, unit);
                }
            } finally {
                lock.unlock();
            }
        }
        return result;
    }

    @Override
    public void done(final Update pUpdate) {
        // Inform delegate before lock is aquired
        delegate.done(pUpdate);

        lock.lock();
        try {
            if (pUpdate.getFailureOrNull() != null) {
                exception = new ExecutionException(pUpdate.getFailureOrNull());
            } else {
                result = pUpdate.getCurrent();
            }
        } finally {
            resultAvailable.signalAll();
            lock.unlock();
        }
    }

    void setExecutingThread(final Thread pExecutingThread) {
        executingThread = pExecutingThread;
    }
}
