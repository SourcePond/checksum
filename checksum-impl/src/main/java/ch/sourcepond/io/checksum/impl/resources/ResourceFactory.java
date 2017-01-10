package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.api.Resource;
import ch.sourcepond.io.checksum.impl.DisposeCallback;
import ch.sourcepond.io.checksum.impl.tasks.FileChannelSource;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * Created by rolandhauser on 10.01.17.
 */
public class ResourceFactory {
    private final ExecutorService updateExecutor;
    private final ExecutorService observerExecutor;
    private final TaskFactory taskFactory;

    public ResourceFactory(final ExecutorService pUpdateExecutor, final ExecutorService pObserverExecutor, final TaskFactory pTaskFactory) {
        updateExecutor = pUpdateExecutor;
        observerExecutor = pObserverExecutor;
        taskFactory = pTaskFactory;
    }

    public Resource<ChannelSource> newResource(final DisposeCallback pDisposeCallback, final ChannelSource pSource) {
        return new ChannelResource(pDisposeCallback, updateExecutor, new Observers<>(observerExecutor, pSource), taskFactory);
    }

    public Resource<Path> newResource(final DisposeCallback pDisposeCallback, final Path pSource) {
        final FileChannelSource source = new FileChannelSource(pSource);
        final Observers<ChannelSource> observers = new Observers<>(observerExecutor, source);
        final ObserversAdapter<Path, ChannelSource> adapter = new ObserversAdapter<Path, ChannelSource>(observers, pSource);
        return new ChannelResource(pDisposeCallback, updateExecutor, adapter, taskFactory);
    }
}
