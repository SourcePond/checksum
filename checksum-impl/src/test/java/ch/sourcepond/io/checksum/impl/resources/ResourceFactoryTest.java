package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.*;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.store.DisposeCallback;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

/**
 * Created by rolandhauser on 11.01.17.
 */
public class ResourceFactoryTest {
    private final ExecutorService updateExecutor = mock(ExecutorService.class);
    private final ExecutorService observerExecutor = mock(ExecutorService.class);
    private final Callable<Checksum> task = mock(Callable.class);
    private final TaskFactory taskFactory = mock(TaskFactory.class);
    private final DisposeCallback disposeCallback = mock(DisposeCallback.class);
    private final DigesterPool digesterPool = mock(DigesterPool.class);
    private final ChannelSource channelSource = mock(ChannelSource.class);
    private final StreamSource streamSource = mock(StreamSource.class);
    private Path path;
    private URL url;
    private ResourceFactory factory = new ResourceFactory(updateExecutor, observerExecutor, taskFactory);

    @Before
    public void setup() throws Exception {
        path = FileSystems.getDefault().getPath("src", "test", "resources", "testfile_01.txt");
        url = getClass().getResource("/testfile_01.txt");
        when(taskFactory.newChannelTask(same(digesterPool), notNull(), same(MILLISECONDS), eq(0L))).thenReturn(task);
        when(taskFactory.newStreamTask(same(digesterPool), notNull(), same(MILLISECONDS), eq(0L))).thenReturn(task);
    }

    @Test
    public void newChannelResource() {
        final LeasableResource<ChannelSource> res = factory.newResource(disposeCallback, digesterPool, channelSource);
        res.update();
        verify(updateExecutor).submit(task);

        // Should not cause a NullPointerException
        res.addCancelObserver(mock(CancelObserver.class));
        res.addSuccessObserver(mock(SuccessObserver.class));
        res.addFailureObserver(mock(FailureObserver.class));
    }

    @Test
    public void newStreamResource() {
        final LeasableResource<StreamSource> res = factory.newResource(disposeCallback, digesterPool, streamSource);
        res.update();
        verify(updateExecutor).submit(task);

        // Should not cause a NullPointerException
        res.addCancelObserver(mock(CancelObserver.class));
        res.addSuccessObserver(mock(SuccessObserver.class));
        res.addFailureObserver(mock(FailureObserver.class));
    }

    @Test
    public void newPathResource() {
        final LeasableResource<Path> res = factory.newResource(disposeCallback, digesterPool, path);
        res.update();
        verify(updateExecutor).submit(task);

        // Should not cause a NullPointerException
        res.addCancelObserver(mock(CancelObserver.class));
        res.addSuccessObserver(mock(SuccessObserver.class));
        res.addFailureObserver(mock(FailureObserver.class));
    }

    @Test
    public void newUrlResource() {
        final LeasableResource<URL> res = factory.newResource(disposeCallback, digesterPool, url);
        res.update();
        verify(updateExecutor).submit(task);

        // Should not cause a NullPointerException
        res.addCancelObserver(mock(CancelObserver.class));
        res.addSuccessObserver(mock(SuccessObserver.class));
        res.addFailureObserver(mock(FailureObserver.class));
    }

}
