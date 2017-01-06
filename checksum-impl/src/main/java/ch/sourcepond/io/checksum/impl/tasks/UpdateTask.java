package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.impl.ResourceCallback;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import org.slf4j.Logger;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by rolandhauser on 06.01.17.
 */
abstract class UpdateTask implements Runnable {
    private static final Logger LOG = getLogger(UpdateTask.class);
    static final byte[] CANCEL_DIGEST = new byte[0];
    protected final ResourceCallback resource;
    protected final DataAvailability availability;
    private volatile boolean cancelled;

    UpdateTask(final ResourceCallback pResource, final long pInterval, final TimeUnit pUnit) {
        resource = pResource;
        availability = new DataAvailability(pUnit, pInterval);
    }

    boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        cancelled = true;
    }

    abstract void newDigest(MessageDigest pDigest) throws IOException;

    @Override
    public void run() {
        final DigesterPool digesterPool = resource.getDigesterPool();
        final MessageDigest digest = digesterPool.get();
        try {
            newDigest(digest);
            if (isCancelled()) {
                resource.informCancelObservers();
            } else {
                resource.informSuccessObservers(new ChecksumImpl(digest.digest()));
            }
        } catch (final IOException e) {
            resource.informFailureObservers(e);
        } finally {
            digesterPool.release(digest);
        }
    }
}
