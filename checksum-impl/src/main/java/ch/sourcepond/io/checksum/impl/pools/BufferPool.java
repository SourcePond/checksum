package ch.sourcepond.io.checksum.impl.pools;

import java.nio.ByteBuffer;

/**
 * Created by rolandhauser on 05.01.17.
 */
public class BufferPool extends BasePool<ByteBuffer> {

    BufferPool() {
    }

    @Override
    ByteBuffer newPooledObject() {
        return ByteBuffer.allocateDirect(8192);
    }

    @Override
    void pooledObjectReleased(final ByteBuffer pPooledObject) {
        pPooledObject.clear();
    }
}
