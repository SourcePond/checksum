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
package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;
import ch.sourcepond.io.checksum.impl.tasks.UpdateTask;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Resource implementation which works with any kind of {@link java.io.InputStream}.
 */
class StreamResource extends BaseResource<StreamSource> {

    public StreamResource(final ScheduledExecutorService pUpdateExecutor,
                          final StreamSource pAccessor,
                          final DigesterPool pDigesterPool,
                          final TaskFactory pTaskFactory) {
        super(pUpdateExecutor, pAccessor, pDigesterPool, pTaskFactory);
    }

    @Override
    UpdateTask<StreamSource> newUpdateTask(final TimeUnit pUnit, final long pInterval) throws IOException {
        return taskFactory.newStreamTask(digesterPool, this, pUnit, pInterval);
    }
}
