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
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.interrupted;
import static java.time.Instant.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Base task for updating a {@link MessageDigest}.
 */
public abstract class UpdateTask<A> implements Closeable, Runnable {

    @FunctionalInterface
    interface Reader {

        int read() throws IOException;
    }

    @FunctionalInterface
    interface Updater {

        void update(int pReadBytes);
    }

    private static final Logger LOG = getLogger(UpdateTask.class);
    private static final int EOF = -1;
    private final ScheduledExecutorService executor;
    private final DigesterPool digesterPool;
    private final ResultFuture future;
    private volatile byte numOfReSchedules;
    final MessageDigest digest;
    final BaseResource<A> resource;
    private final TimeUnit unit;
    private final long delay;

    UpdateTask(final ScheduledExecutorService pExecutor,
               final DigesterPool pDigesterPool,
               final ResultFuture pFuture,
               final BaseResource<A> pResource,
               final TimeUnit pUnit,
               final long pInterval) {
        executor = pExecutor;
        digesterPool = pDigesterPool;
        future = pFuture;
        resource = pResource;
        digest = pDigesterPool.get();
        unit = pUnit;
        delay = pInterval;
    }

    abstract boolean updateDigest() throws InterruptedException, IOException;

    private static void checkInterrupted() throws InterruptedException {
        if (interrupted()) {
            throw new InterruptedException();
        }
    }

    boolean read(final Reader pReader, final Updater pUpdater) throws InterruptedException, IOException {
        checkInterrupted();
        final int rc;
        int readBytes = pReader.read();
        if (readBytes == EOF) {
            // Increment iterations if currently no more data is available.
            rc = ++numOfReSchedules;
        } else {
            // If more data is available, reset iterations to zero.
            rc = numOfReSchedules = 0;
            pUpdater.update(readBytes);
        }

        while ((readBytes = pReader.read()) != -1) {
            checkInterrupted();
            pUpdater.update(readBytes);
        }

        return 1 >= rc && delay > 0;
    }

    @Override
    public final void run() {
        Exception failureOrNull = null;
        try {
            if (updateDigest()) {
                // Re-schedule this task somewhen in the future and...
                executor.schedule(this, delay, unit);

                // ... finish current execution at this point after reschedule
                return;
            }
        } catch (final Exception e) {
            failureOrNull = e;
        }

        finalizeResult(failureOrNull);
    }

    private void finalizeResult(final Exception pFailureOrNull) {
        final byte[] checksum = digest.digest();
        final Checksum current;
        final Checksum previous;

        // Mutex on resource, getCurrent and setCurrent
        // must be synchronized externally.
        synchronized (resource) {
            previous = resource.getCurrent();
            if (pFailureOrNull == null) {
                current = new ChecksumImpl(now(), checksum);
                resource.setCurrent(current);
            } else {
                current = previous;
            }
        }

        try {
            future.done(new UpdateImpl(previous, current, pFailureOrNull));
        } finally {
            try {
                close();
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            } finally {
                digesterPool.release(digest);
            }
        }
    }
}
