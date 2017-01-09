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

import ch.sourcepond.io.checksum.impl.ResourceContext;
import ch.sourcepond.io.checksum.impl.pools.Pool;
import org.slf4j.Logger;

import java.io.IOException;
import java.security.MessageDigest;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Base task for updating a {@link MessageDigest}.
 */
abstract class UpdateTask implements Runnable {
    private static final Logger LOG = getLogger(UpdateTask.class);
    static final byte[] CANCEL_DIGEST = new byte[0];
    protected final ResourceContext resource;
    protected final DataReader reader;
    private volatile boolean cancelled;

    UpdateTask(final ResourceContext pResource, final DataReader pReader) {
        resource = pResource;
        reader = pReader;
    }

    public final void cancel() {
        reader.cancel();
    }

    abstract void updateDigest(MessageDigest pDigest) throws IOException, CancelException;

    @Override
    public final void run() {
        final Pool<MessageDigest> digesterPool = resource.getDigesterPool();
        final MessageDigest digest = digesterPool.get();
        try {
            updateDigest(digest);
            resource.informSuccessObservers(resource.newChecksum(digest.digest()));
        } catch (final CancelException e) {
            resource.informCancelObservers();
        } catch (final IOException e) {
            resource.informFailureObservers(e);
        } finally {
            digesterPool.release(digest);
        }
    }
}
