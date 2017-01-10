package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.impl.pools.Pool;
import org.junit.Before;

import java.nio.ByteBuffer;
import java.nio.file.FileSystems;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by rolandhauser on 09.01.17.
 */
public class ChannelUpdateTaskTest extends UpdateTaskTest {
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private final Pool<ByteBuffer> bufferPool = mock(Pool.class);

    @Override
    protected UpdateTask newTask() {
        return new ChannelUpdateTask(digesterPool, resource, reader, new FileChannelSource(
                FileSystems.getDefault().getPath(getClass().getResource("/testfile_01.txt").getFile())), bufferPool);
    }

    @Before
    public void setup() throws Exception {
        super.setup();
        when(bufferPool.get()).thenReturn(buffer);
    }
}
