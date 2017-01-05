package ch.sourcepond.io.checksum.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by rolandhauser on 05.01.17.
 */
final class FileResource extends BaseResource {
    private static final Logger LOG = LoggerFactory.getLogger(FileResource.class);
    private static final ReferenceQueue<Reference<?>> clearedReferences = new ReferenceQueue<>();
    private static final LinkedList<WeakReference<ByteBuffer>> buffers = new LinkedList<>();
    private final Path file;

    public FileResource(final ObservedResourcesRegistryImpl pRegistry,
                        final ExecutorService pUpdateExecutor,
                        final MessageDigest pDigest,
                        final Path file) {
        super(pRegistry, pUpdateExecutor, pDigest);
        this.file = file;
    }

    private static synchronized ByteBuffer getBuffer() {
        ByteBuffer buffer = null;
        if (!buffers.isEmpty()) {
            buffer = buffers.removeFirst().get();
        }

        if (buffer == null) {
            buffer = ByteBuffer.allocateDirect(8192);
        }

        return buffer;
    }

    private static synchronized void releaseBuffer(final ByteBuffer pBuffer) {
        Reference<?> ref = clearedReferences.poll();
        while (ref != null) {
            buffers.remove(ref);
            ref = clearedReferences.poll();
        }
        pBuffer.clear();
        buffers.add(new WeakReference<ByteBuffer>(pBuffer));
    }

    private boolean isCancelled() {
        return false;
    }

    @Override
    void doUpdate(final long pInterval, final TimeUnit pUnit) throws IOException {
        final ByteBuffer buffer = getBuffer();
        try (final FileChannel ch = FileChannel.open(file, StandardOpenOption.READ)) {
            boolean dataAvailable = true;
            while (!isCancelled()) {
                if (ch.read(buffer) != -1) {
                    digest.update(buffer);
                    buffer.clear();
                    if (!dataAvailable) {
                        dataAvailable = true;
                    }
                    continue;
                } else if (dataAvailable) {
                    dataAvailable = false;
                    try {
                        pUnit.sleep(pInterval);
                        continue;
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                        LOG.warn(e.getMessage(), e);
                        break;
                    }
                }
                break;
            }
        } finally {
            releaseBuffer(buffer);
        }
    }
}
