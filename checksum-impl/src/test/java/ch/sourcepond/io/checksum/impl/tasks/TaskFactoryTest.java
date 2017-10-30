package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.impl.pools.BufferPool;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
@SuppressWarnings({"unchecked", "FieldCanBeLocal"})
public class TaskFactoryTest {
    private final ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
    private final DigesterPool digesterPool = mock(DigesterPool.class);
    private final BufferPool bufferPool = mock(BufferPool.class);
    private final TaskFactory factory = new TaskFactory(bufferPool);
    private final UpdateObserver observer = mock(UpdateObserver.class);
    private URL anyUrl;

    @Before
    public void setup() throws Exception {
        anyUrl = new URL("file:///any/path");
        factory.setUpdateExecutor(executor);
    }

    private void assertNotSame(final Runnable first, final Runnable second) {
        assertNotNull(first);
        assertNotNull(second);
        Assert.assertNotSame(first, second);
    }

    @Test
    public void verifyChannelTaskFactoryMethod() throws IOException {
        final ChannelSource channelSource = mock(ChannelSource.class);
        final ReadableByteChannel channel = mock(ReadableByteChannel.class);
        final BaseResource<ChannelSource> resource = mock(BaseResource.class);
        when(channelSource.openChannel()).thenReturn(channel);
        when(resource.getSource()).thenReturn(channelSource);
        assertNotSame(factory.newChannelTask(observer, digesterPool, resource, TimeUnit.SECONDS, 1L),
                factory.newChannelTask(observer, digesterPool, resource, TimeUnit.SECONDS, 1L));
    }

    @Test
    public void verifyStreamTaskFactoryMethod() throws IOException {
        final StreamSource streamSource = mock(StreamSource.class);
        final InputStream stream = mock(InputStream.class);
        final BaseResource<StreamSource> resource = mock(BaseResource.class);
        when(streamSource.openStream()).thenReturn(stream);
        when(resource.getSource()).thenReturn(streamSource);
        assertNotSame(factory.newStreamTask(observer, digesterPool, resource, TimeUnit.SECONDS, 1L),
                factory.newStreamTask(observer, digesterPool, resource, TimeUnit.SECONDS, 1L));
    }
}
