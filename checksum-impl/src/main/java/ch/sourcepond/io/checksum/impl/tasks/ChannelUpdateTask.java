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
import ch.sourcepond.io.checksum.impl.resources.Observable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;

/**
 * Updater-task which fetches its data from a {@link ReadableByteChannel} instance.
 */
class ChannelUpdateTask<S> extends UpdateTask<S, ChannelSource> {
    private final BufferPool bufferPool;

    ChannelUpdateTask(final DigesterPool pDigesterPool,
                      final Observable<S, ChannelSource> pResource,
                      final DataReader pReader,
                      final BufferPool pBufferPool) {
        super(pDigesterPool, pResource, pReader);
        bufferPool = pBufferPool;
    }

    @Override
    void updateDigest(final MessageDigest pDigest) throws InterruptedException, IOException {
        final ByteBuffer buffer = bufferPool.get();
        try (final ReadableByteChannel ch = resource.getAccessor().openChannel()) {
            reader.read(() -> ch.read(buffer), readBytes -> {
                buffer.flip();
                pDigest.update(buffer);
                buffer.clear();
            });
        } finally {
            bufferPool.release(buffer);
        }
    }
}