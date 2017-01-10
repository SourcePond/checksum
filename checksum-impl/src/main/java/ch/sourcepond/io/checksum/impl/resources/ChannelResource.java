package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.impl.DisposeCallback;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 */
class ChannelResource<S> extends BaseResource<S, ChannelSource> {

    ChannelResource(final DisposeCallback pDisposeCallback,
                           final ExecutorService pUpdateExecutor,
                           final Observers<S, ChannelSource> pObservers,
                           final TaskFactory pTaskFactory) {
        super(pDisposeCallback, pUpdateExecutor, pObservers, pTaskFactory);
    }

    @Override
    Callable<Checksum> newUpdateTask(final TimeUnit pUnit, final long pInterval) {
        return taskFactory.newChannelTask(observers, pUnit, pInterval);
    }
}
