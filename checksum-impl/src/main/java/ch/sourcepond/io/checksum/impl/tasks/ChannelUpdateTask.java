package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.impl.ResourceCallback;
import ch.sourcepond.io.checksum.impl.pools.BufferPool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

/**
 * Created by rolandhauser on 06.01.17.
 */
final class ChannelUpdateTask extends UpdateTask {
    private final ChannelSource channelSource;

    public ChannelUpdateTask(final ResourceCallback pResource, final long pInterval, final TimeUnit pUnit, final ChannelSource pChannelSource) {
        super(pResource, pInterval, pUnit);
        this.channelSource = pChannelSource;
    }

    @Override
    void newDigest(final MessageDigest pDigest) throws IOException {
        try (final ReadableByteChannel ch = channelSource.openChannel()) {
            final BufferPool bufferPool = resource.getBufferPool();
            final ByteBuffer buffer = bufferPool.get();
            try {
                while (availability.available()) {
                    if (availability.readBytes(ch.read(buffer)) != -1) {
                        pDigest.update(buffer);
                        buffer.clear();
                    }
                }
            } finally {
                bufferPool.release(buffer);
            }
        }
    }
}
