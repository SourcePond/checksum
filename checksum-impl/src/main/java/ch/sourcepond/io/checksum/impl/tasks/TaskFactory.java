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
import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.impl.pools.BufferPool;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.currentThread;

/**
 *
 */
public class TaskFactory {
    private final BufferPool bufferPool;

    // Constructor used by BundleActivator
    public TaskFactory() {
        this(new BufferPool());
    }

    // Constructor used for testing
    public TaskFactory(final BufferPool pBufferPool) {
        bufferPool = pBufferPool;
    }

    public <S> Callable<Checksum> newChannelTask(final DigesterPool digesterPool, final UpdateObserver pObserver, final BaseResource<ChannelSource> pResource, final TimeUnit pUnit, final long pInterval) throws IOException {
        return new ChannelUpdateTask(digesterPool, new ResultFuture(currentThread(), pObserver), pObserver, pResource, new DataReader(pUnit, pInterval), bufferPool);
    }

    public <S> Callable<Checksum> newStreamTask(final DigesterPool digesterPool, final UpdateObserver pObserver, final BaseResource<StreamSource> pResource, final TimeUnit pUnit, final long pInterval) throws IOException {
        return new StreamUpdateTask(digesterPool, new ResultFuture(currentThread(), pObserver), pObserver, pResource, new DataReader(pUnit, pInterval));
    }
}
