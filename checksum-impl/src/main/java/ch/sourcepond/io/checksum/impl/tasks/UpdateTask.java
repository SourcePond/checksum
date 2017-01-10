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
import ch.sourcepond.io.checksum.impl.pools.Pool;
import ch.sourcepond.io.checksum.impl.resources.Observable;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.Callable;

/**
 * Base task for updating a {@link MessageDigest}.
 */
public abstract class UpdateTask<S, A> implements Callable<Checksum> {
    private final Pool<MessageDigest> digesterPool;
    protected final Observable<S, A> resource;
    protected final DataReader reader;

    UpdateTask(final Pool<MessageDigest> pDigesterPool, final Observable<S, A> pResource, final DataReader pReader) {
        digesterPool = pDigesterPool;
        resource = pResource;
        reader = pReader;
    }

    abstract void updateDigest(MessageDigest pDigest) throws InterruptedException, IOException;

    @Override
    public final Checksum call() throws Exception {
        final MessageDigest digest = digesterPool.get();
        try {
            updateDigest(digest);
            final Checksum checksum = new ChecksumImpl(digest.digest());
            resource.informSuccessObservers(checksum);
            return checksum;
        } catch (final InterruptedException e) {
            resource.informCancelObservers();
            throw e;
        } catch (final IOException e) {
            resource.informFailureObservers(e);
            throw e;
        } finally {
            digesterPool.release(digest);
        }
    }
}
