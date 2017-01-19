package ch.sourcepond.io.checksum.impl.tasks;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.*;

import static java.lang.Thread.currentThread;
import static org.junit.Assert.*;

/**
 *
 */
public class DataReaderTest {

    private class FiniteDataReader {
        private final int maxIterations;
        private volatile int iteration = 1;

        FiniteDataReader() {
            maxIterations = 10;
        }

        FiniteDataReader(@SuppressWarnings("SameParameterValue") final int pMaxIterations) {
            maxIterations = pMaxIterations;
        }

        int getReadBytes() {
            if (iteration++ % 2 == 0 || iteration >= maxIterations) {
                return -1;
            }
            return ANY_READ_BYTES;
        }
    }

    private static final int ANY_READ_BYTES = 10;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final DataReader reader = new DataReader(TimeUnit.MILLISECONDS, 500L);
    private volatile Exception expectedInterruptedException;

    @After
    public void tearDown() {
        executor.shutdown();
    }

    @Test(timeout = 200)
    public void skipWaitWhenIntervalIsLowerOrEqualZero() throws Exception {
        final DataReader reader = new DataReader(TimeUnit.MILLISECONDS, 0);
        final FiniteDataReader source = new FiniteDataReader(5);
        reader.read(source::getReadBytes, readBytes -> {
        });
    }

    @Test(timeout = 3000)
    public void doNotWaitWhenDataIsAvailable() throws Exception {
        final FiniteDataReader source = new FiniteDataReader(5);
        executor.submit(() -> {
                    reader.read(source::getReadBytes, readBytes -> {
                    });
                    return null;
                }
        ).get();
    }

    @Test(timeout = 3000)
    public void throwInterruptedExceptionWhenCancelled() throws Exception {
        final FiniteDataReader source = new FiniteDataReader();
        final CountDownLatch latch = new CountDownLatch(1);
        executor.execute(() -> {
                currentThread().interrupt();
                try {
                    reader.read(source::getReadBytes, readBytes -> {
                    });
                } catch (Exception e) {
                    expectedInterruptedException = e;
                }
                latch.countDown();
            }
        );

        latch.await();
        assertNotNull(expectedInterruptedException);
        assertTrue(expectedInterruptedException instanceof InterruptedException);
    }

    @Test(timeout = 3000)
    public void throwCancelExceptionWhenThreadInterruptedDuringSleep() throws Exception {
        final FiniteDataReader source = new FiniteDataReader();
        final Future<Object> f = executor.submit(() -> {

                final Thread outer = currentThread();
                executor.schedule(outer::interrupt, 100L, TimeUnit.MILLISECONDS);
                reader.read(source::getReadBytes, readBytes -> {
                });
                return null;
            }
        );

        try {
            f.get();
            fail("Exception expected");
        } catch (final ExecutionException e) {
            assertTrue(e.getCause() instanceof InterruptedException);
        }
    }
}
