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

import ch.sourcepond.io.checksum.api.*;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Abstract base implementation of the {@link Resource} interface.
 *
 * @param <A> Type of the accessor necessary to read from the source. This can either be
 *            {@link ChannelSource} or {@link StreamSource}.
 */
public abstract class BaseResource<A> implements Resource {
    private final ExecutorService updateExecutor;
    private final A source;
    final DigesterPool digesterPool;
    final TaskFactory taskFactory;
    private Checksum current;

    BaseResource(final ExecutorService pUpdateExecutor,
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
    public final Future<Checksum> update(final CalculationObserver pObserver) {
        return update(0L, pObserver);
    }

    @Override
    public final Future<Checksum> update(final long pIntervalInMilliseconds, final CalculationObserver pObserver) {
        return update(MILLISECONDS, pIntervalInMilliseconds, pObserver);
    }

    @Override
    public final Future<Checksum> update(final TimeUnit pUnit, final long pInterval, final CalculationObserver pObserver) {
        return updateExecutor.submit(newUpdateTask(pUnit, pInterval, pObserver));
    }

    abstract Callable<Checksum> newUpdateTask(TimeUnit pUnit, long pInterval, final CalculationObserver pObserver);

    public synchronized Checksum updateChecksum(final Checksum pCurrent) {
        final Checksum previous = current;
        current = pCurrent;
        return previous;
    }
}
