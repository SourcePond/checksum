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
import java.util.concurrent.Callable;

import static java.lang.Thread.currentThread;
import static java.time.Instant.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Base task for updating a {@link MessageDigest}.
 */
public abstract class UpdateTask<A> implements Closeable, Callable<Checksum> {

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
    private final DigesterPool digesterPool;
    private final ResultFuture future;
    private volatile byte numOfReSchedules;
    private final MessageDigest digest;
    final BaseResource<A> resource;
    final DataReader reader;

    UpdateTask(final DigesterPool pDigesterPool,
               final ResultFuture pFuture,
               final BaseResource<A> pResource,
               final DataReader pReader) {
        digesterPool = pDigesterPool;
        future = pFuture;
        resource = pResource;
        reader = pReader;
        digest = pDigesterPool.get();
    }

    abstract void updateDigest(MessageDigest pDigest) throws InterruptedException, IOException;

    boolean read(final Reader pReader, final Updater pUpdater) throws IOException {
        if (!currentThread().isInterrupted()) {
            final int rc;
            int readBytes = pReader.read();
            if (readBytes == EOF) {
                // Increment iterations if currently no more data is available.
                rc = numOfReSchedules++;
            } else {
                // If more data is available, reset iterations to zero.
                rc = numOfReSchedules = 0;
                pUpdater.update(readBytes);
            }

            while (!currentThread().isInterrupted() && (readBytes = pReader.read()) != -1) {
                pUpdater.update(readBytes);
            }

            return 1 >= rc;
        }
        return false;
    }

    @Override
    public final Checksum call() {
        Throwable failureOrNull = null;
        try {
            updateDigest(digest);
        } catch (final Throwable e) {
            failureOrNull = e;
        }

        final byte[] checksum = digest.digest();
        final Checksum current;
        final Checksum previous;

        // Mutex on resource, getCurrent and setCurrent
        // must be synchronized externally.
        synchronized (resource) {
            previous = resource.getCurrent();
            if (failureOrNull == null) {
                current = new ChecksumImpl(now(), checksum);
                resource.setCurrent(current);
            } else {
                current = previous;
            }
        }

        try {
            future.done(new UpdateImpl(previous, current, failureOrNull));
        } finally {
            try {
                close();
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            } finally {
                digesterPool.release(digest);
            }
        }
        return current;
    }
}
