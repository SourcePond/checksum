package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.impl.pools.BufferPool;
import ch.sourcepond.io.checksum.impl.resources.FileChannelSource;
import org.junit.Before;

import java.io.IOException;
import java.nio.ByteBuffer;

import static java.nio.ByteBuffer.allocate;
import static java.nio.file.FileSystems.getDefault;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ChannelUpdateTaskTest extends UpdateTaskTest<ChannelSource> {
    private final ByteBuffer buffer = allocate(1024);
    private final BufferPool bufferPool = mock(BufferPool.class);

    @Override
    protected UpdateTask<ChannelSource> newTask() throws IOException {
        return new ChannelUpdateTask(executor, digesterPool, observer, resource, bufferPool, SECONDS, 1);
    }

    @Before
    public void setup() throws Exception {
        when(bufferPool.get()).thenReturn(buffer);
        when(resource.getSource()).thenReturn(new FileChannelSource(
                getDefault().getPath(getClass().getResource("/testfile_01.txt").getFile())));
        super.setup();
    }
}
