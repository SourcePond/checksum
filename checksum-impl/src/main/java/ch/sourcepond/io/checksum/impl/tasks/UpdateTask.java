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

import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.Callable;

import static java.time.Instant.now;

/**
 * Base task for updating a {@link MessageDigest}.
 */
public abstract class UpdateTask<A> implements Callable<Checksum> {
    private final DigesterPool digesterPool;
    private final UpdateObserver observer;
    final BaseResource<A> resource;
    final DataReader reader;

    UpdateTask(final DigesterPool pDigesterPool, final UpdateObserver pObserver, final BaseResource<A> pResource, final DataReader pReader) {
        digesterPool = pDigesterPool;
        observer = pObserver;
        resource = pResource;
        reader = pReader;
    }

    abstract void updateDigest(MessageDigest pDigest) throws InterruptedException, IOException;

    @Override
    public final Checksum call() {
        final MessageDigest digest = digesterPool.get();
        try {
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

            observer.done(new UpdateImpl(previous, current, failureOrNull));
            return current;
        } finally {
            digesterPool.release(digest);
        }
    }
}
