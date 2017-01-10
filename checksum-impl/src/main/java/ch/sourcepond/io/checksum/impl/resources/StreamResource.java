package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.DisposeCallback;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by rolandhauser on 10.01.17.
 */
class StreamResource extends BaseResource<StreamSource> {

    public StreamResource(final DisposeCallback pDisposeCallback,
                          final ExecutorService pUpdateExecutor,
                          final Observers<StreamSource> pObservers,
                          final TaskFactory pTaskFactory) {
        super(pDisposeCallback, pUpdateExecutor, pObservers, pTaskFactory);
    }

    @Override
    Callable<Checksum> newUpdateTask(final TimeUnit pUnit, final long pInterval) {
        return taskFactory.newStreamTask(observers, pUnit, pInterval);
    }
}
