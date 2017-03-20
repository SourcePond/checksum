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

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.impl.pools.BufferPool;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Updater-task which fetches its data from a {@link ReadableByteChannel} instance.
 */
class ChannelUpdateTask extends UpdateTask<ChannelSource> {
    private final BufferPool bufferPool;
    private final ReadableByteChannel channel;

    ChannelUpdateTask(final ScheduledExecutorService pExecutor,
                      final DigesterPool pDigesterPool,
                      final ResultFuture pFuture,
                      final BaseResource<ChannelSource> pResource,
                      final BufferPool pBufferPool,
                      final TimeUnit pUnit,
                      final long pDelay) throws IOException {
        super(pExecutor, pDigesterPool, pFuture, pResource, pUnit, pDelay);
        bufferPool = pBufferPool;
        channel = pResource.getSource().openChannel();
    }

    @Override
    boolean updateDigest() throws InterruptedException, IOException {
        final ByteBuffer buffer = bufferPool.get();
        try {
            return read(() -> channel.read(buffer), readBytes -> {
                buffer.flip();
                digest.update(buffer);
                buffer.clear();
            });
        } finally {
            bufferPool.release(buffer);
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
