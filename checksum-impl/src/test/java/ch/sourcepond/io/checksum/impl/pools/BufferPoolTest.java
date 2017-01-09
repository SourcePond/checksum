package ch.sourcepond.io.checksum.impl.pools;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * Created by rolandhauser on 06.01.17.
 */
public class BufferPoolTest extends BasePoolTest<ByteBuffer> {

    @Override
    protected Pool<ByteBuffer> newTestPool() {
        return new BufferPool();
    }

    @Test
    public void cleanBufferAfterRelease() {
        ByteBuffer buffer = pool.get();
        buffer.put((byte)1);
        assertEquals(1, buffer.position());
        pool.release(buffer);
        assertEquals(0, buffer.position());
    }
}
