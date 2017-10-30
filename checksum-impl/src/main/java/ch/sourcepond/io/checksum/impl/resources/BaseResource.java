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

import ch.sourcepond.io.checksum.api.Algorithm;
import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.Resource;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;
import ch.sourcepond.io.checksum.impl.tasks.UpdateTask;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Abstract base implementation of the {@link Resource} interface.
 *
 * @param <A> Type of the accessor necessary to read from the source. This can either be
 *            {@link ChannelSource} or {@link StreamSource}.
 */
public abstract class BaseResource<A> implements Resource {
    private static final Logger LOG = getLogger(BaseResource.class);
    final ScheduledExecutorService updateExecutor;
    private final A source;
    final DigesterPool digesterPool;
    final TaskFactory taskFactory;
    private Checksum current;

    BaseResource(final ScheduledExecutorService pUpdateExecutor,
                 final A pSource,
                 final DigesterPool pDigesterPool,
                 final TaskFactory pTaskFactory) {
        updateExecutor = pUpdateExecutor;
        source = pSource;
        digesterPool = pDigesterPool;
        taskFactory = pTaskFactory;
    }

    public A getSource() {
        return source;
    }

    @Override
    public final Algorithm getAlgorithm() {
        return digesterPool.getAlgorithm();
    }

    @Override
    public final void update(final UpdateObserver pObserver) throws IOException {
        update(0L, pObserver);
    }

    @Override
    public final void update(final long pIntervalInMilliseconds, final UpdateObserver pObserver) throws IOException {
        update(MILLISECONDS, pIntervalInMilliseconds, pObserver);
    }

    @Override
    public final void update(final TimeUnit pUnit, final long pInterval, final UpdateObserver pObserver) throws IOException {
        final UpdateTask<A> task = newUpdateTask(pObserver, pUnit, pInterval);
        updateExecutor.execute(task);
    }

    Resource initialUpdate() {
        final InitialChecksum initialChecksum = new InitialChecksum();
        synchronized (this) {
            setCurrent(initialChecksum);
        }
        try {
            update(initialChecksum);
        } catch (final IOException e) {
            LOG.warn(e.getMessage(), e);
            initialChecksum.initDefaults();
        }
        return this;
    }

    abstract UpdateTask<A> newUpdateTask(UpdateObserver pObserver, TimeUnit pUnit, long pInterval) throws IOException;

    public synchronized Checksum getCurrent() {
        return current;
    }

    // Not thread-safe, must be synchronized externally
    public void setCurrent(final Checksum pCurrent) {
        current = pCurrent;
    }
}
