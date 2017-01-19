package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.impl.pools.BufferPool;
import ch.sourcepond.io.checksum.impl.resources.FileChannelSource;
import org.junit.Before;

import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ChannelUpdateTaskTest extends UpdateTaskTest<Path, ChannelSource> {
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private final BufferPool bufferPool = mock(BufferPool.class);

    @Override
    protected UpdateTask<Path, ChannelSource> newTask() {
        when(resource.getAccessor()).thenReturn(new FileChannelSource(
                FileSystems.getDefault().getPath(getClass().getResource("/testfile_01.txt").getFile())));
        return new ChannelUpdateTask<>(digesterPool, resource, reader, bufferPool);
    }

    @Before
    public void setup() throws Exception {
        super.setup();
        when(bufferPool.get()).thenReturn(buffer);
    }
}
