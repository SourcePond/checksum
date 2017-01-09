package ch.sourcepond.io.checksum.impl.tasks;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.*;

import static java.lang.Thread.currentThread;
import static org.junit.Assert.*;

/**
 * Created by rolandhauser on 09.01.17.
 */
public class DataReaderTest {

    private class FiniteDataReader {
        private final int maxIterations;
        private volatile int iteration = 1;

        FiniteDataReader() {
            maxIterations = 10;
        }

        FiniteDataReader(final int pMaxIterations) {
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
    private final DataReader reader = new DataReader(TimeUnit.MILLISECONDS, 500l);

    @After
    public void tearDown() {
        executor.shutdown();
    }

    @Test(timeout = 3000)
    public void doNotWaitWhenDataIsAvailable() throws Exception {
        final FiniteDataReader source = new FiniteDataReader();
        executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                reader.read(() -> source.getReadBytes(), readBytes -> {});
                return null;
            }
        }).get();
    }

    @Test(timeout = 3000)
    public void throwCancelExceptionWhenCancelled() throws Exception {
        final FiniteDataReader source = new FiniteDataReader();
        final Future<Object> f = executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                reader.read(() -> -1, readBytes -> {});
                return null;
            }
        });

        // Cancel explicitly
        reader.cancel();

        try {
            f.get();
            fail("Exception expected");
        } catch (final ExecutionException e) {
            assertTrue(e.getCause() instanceof CancelException);
            final CancelException c = (CancelException) e.getCause();
            assertNull(c.getCause());
        }
    }

    @Test(timeout = 3000)
    public void throwCancelExceptionWhenThreadInterruptedDuringSleep() throws Exception {
        final FiniteDataReader source = new FiniteDataReader();
        final Future<Object> f = executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                final Thread outer = currentThread();
                executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        outer.interrupt();
                    }
                }, 100L, TimeUnit.MILLISECONDS);
                reader.read(() -> source.getReadBytes(), readBytes -> {});
                return null;
            }
        });

        try {
            f.get();
            fail("Exception expected");
        } catch (final ExecutionException e) {
            assertTrue(e.getCause() instanceof CancelException);
            final CancelException c = (CancelException) e.getCause();
            assertTrue(c.getCause() instanceof InterruptedException);
        }
    }
}
