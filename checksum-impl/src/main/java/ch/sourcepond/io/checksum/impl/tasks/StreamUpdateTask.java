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

import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 */
final class StreamUpdateTask extends UpdateTask<StreamSource> {
    private final InputStream in;

    StreamUpdateTask(final ScheduledExecutorService pExecutor,
                     final DigesterPool pDigesterPool,
                     final ResultFuture pFuture,
                     final BaseResource<StreamSource> pResource,
                     final TimeUnit pUnit,
                     final long pDelay) throws IOException {
        super(pExecutor, pDigesterPool, pFuture, pResource, pUnit, pDelay);
        in = pResource.getSource().openStream();
    }

    @Override
    boolean updateDigest() throws InterruptedException, IOException {
        final byte[] buffer = new byte[1024];
        return read(() -> in.read(buffer), readBytes -> digest.update(buffer, 0, readBytes));
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
