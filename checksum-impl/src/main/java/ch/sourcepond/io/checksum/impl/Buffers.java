package ch.sourcepond.io.checksum.impl;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by rolandhauser on 05.01.17.
 */
public class Buffers {
    private final ReferenceQueue<Reference<?>> clearedReferences = new ReferenceQueue<>();
    private final LinkedList<WeakReference<ByteBuffer>> buffers = new LinkedList<>();

    public ByteBuffer get() {
        ByteBuffer buffer = null;
        if (!buffers.isEmpty()) {
            buffer = buffers.removeFirst().get();
        }

        if (buffer == null) {
            buffer = ByteBuffer.allocateDirect(8192);
        }

        return buffer;
    }

    public void release(final ByteBuffer pBuffer) {
        Reference<?> ref = clearedReferences.poll();
        while (ref != null) {
            buffers.remove(ref);
            ref = clearedReferences.poll();
        }
        pBuffer.clear();
        buffers.add(new WeakReference<ByteBuffer>(pBuffer));
    }
}
