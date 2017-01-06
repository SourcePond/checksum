package ch.sourcepond.io.checksum.impl.tasks;

import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by rolandhauser on 06.01.17.
 */
final class DataAvailability {
    private static final Logger LOG = getLogger(DataAvailability.class);
    private final TimeUnit unit;
    private final long interval;
    private volatile boolean cancelled;
    private int iterations = 0;

    public DataAvailability(final TimeUnit pUnit, final long pInterval) {
        unit = pUnit;
        interval = pInterval;
    }

    int readBytes(int pReadBytes) {
        iterations = pReadBytes >= 0 ? 0 : iterations++;
        return pReadBytes;
    }

    boolean isCancelled() {
        return cancelled;
    }

    boolean available() {
        if (iterations >= 2) {
            return false;
        }

        if (iterations == 1) {
            try {
                unit.sleep(interval);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.warn(e.getMessage(), e);
            }
        }

        return true;
    }
}
