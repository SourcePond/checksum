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
import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.pools.Pool;
import ch.sourcepond.io.checksum.impl.resources.Observable;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by rolandhauser on 06.01.17.
 */
public class TaskFactory {
    private final Pool<MessageDigest> digesterPool;
    private final Pool<ByteBuffer> bufferPool;

    public TaskFactory(final Pool<MessageDigest> pDigesterPool, final Pool<ByteBuffer> pBufferPool) {
        digesterPool = pDigesterPool;
        bufferPool = pBufferPool;
    }

    public Callable<Checksum> newChannelTask(final Observable<ChannelSource, ChannelSource> pResource, final TimeUnit pUnit, final long pInterval) {
        return new ChannelUpdateTask<ChannelSource>(digesterPool, pResource, new DataReader(pUnit, pInterval), bufferPool);
    }

    public Callable<Checksum> newFileTask(final Observable<Path, ChannelSource> pResource, final TimeUnit pUnit, final long pInterval) {
        return new ChannelUpdateTask<Path>(digesterPool, pResource, new DataReader(pUnit, pInterval), bufferPool);
    }

    public Callable<Checksum> newStreamTask(final Observable<StreamSource, StreamSource> pResource, final TimeUnit pUnit, final long pInterval) {
        return new StreamUpdateTask(digesterPool, pResource, new DataReader(pUnit, pInterval));
    }
}
