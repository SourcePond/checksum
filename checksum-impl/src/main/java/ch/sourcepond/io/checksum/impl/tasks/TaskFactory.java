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
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.impl.pools.BufferPool;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class TaskFactory {
    private final BufferPool bufferPool;

    // Injected by Felix DM
    private ScheduledExecutorService updateExecutor;

    // Constructor used by BundleActivator
    public TaskFactory() {
        bufferPool = new BufferPool();
    }

    // Constructor used for testing
    public TaskFactory(final BufferPool pBufferPool) {
        bufferPool = pBufferPool;
    }

    public void setUpdateExecutor(final ScheduledExecutorService pUpdateExecutor) {
        updateExecutor = pUpdateExecutor;
    }

    public ResultFuture newInitialResult() {
        return null;
    }


    public ResultFuture newResult(final UpdateObserver pObserver) {
        return new ResultFuture(pObserver);
    }

    public UpdateTask<ChannelSource> newChannelTask(final UpdateObserver pObserver,
                                                    final DigesterPool digesterPool,
                                                    final BaseResource<ChannelSource> pResource,
                                                    final TimeUnit pUnit, final long pInterval)
            throws IOException {
        return new ChannelUpdateTask(updateExecutor, digesterPool, pObserver, pResource, bufferPool, pUnit, pInterval);
    }

    public UpdateTask<StreamSource> newStreamTask(final UpdateObserver pObserver,
                                                  final DigesterPool digesterPool,
                                                  final BaseResource<StreamSource> pResource,
                                                  final TimeUnit pUnit,
                                                  final long pInterval)
            throws IOException {
        return new StreamUpdateTask(updateExecutor, digesterPool, pObserver, pResource, pUnit, pInterval);
    }
}
