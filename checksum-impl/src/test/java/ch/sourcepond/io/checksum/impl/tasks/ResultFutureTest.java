/*Copyright (C) 2017 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.Update;
import ch.sourcepond.io.checksum.api.UpdateObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ResultFutureTest {
    private final UpdateObserver observer = mock(UpdateObserver.class);
    private final Update update = mock(Update.class);
    private final Checksum checksum = mock(Checksum.class);
    private final ResultFuture future = new ResultFuture(observer);
    private final CountDownLatch resultAvailable = new CountDownLatch(1);
    private final CountDownLatch workerThreadUp = new CountDownLatch(1);
    private volatile Exception workerException;
    private volatile Exception clientException;
    private volatile CountDownLatch clientThreadUp;
    private volatile Checksum result;
    private Thread client;
    private Thread worker;

    @Before
    public void setup() throws Exception {
        when(update.getCurrent()).thenReturn(checksum);
        worker = new Thread(() -> {
            future.setExecutingThread(currentThread());
            workerThreadUp.countDown();
            try {
                // Simulate long operation
                sleep(1000);
            } catch (final InterruptedException e) {
                workerException = e;
                resultAvailable.countDown();
            }
        });
        worker.start();
        workerThreadUp.await(2, SECONDS);
    }

    @After
    public void tearDown() {
        worker.interrupt();
        if (client != null) {
            client.interrupt();
        }
    }

    private void startClientThread(final Runnable pRunnable) throws Exception {
        clientThreadUp = new CountDownLatch(1);
        client = new Thread(pRunnable);
        client.start();
        clientThreadUp.await(5, SECONDS);
    }

    private void verifyBeforeCancel() {
        assertFalse(future.isCancelled());
        assertFalse(future.isDone());
    }

    private void verifyAfterCancel() {
        assertTrue(future.isCancelled());
        assertTrue(future.isDone());
    }

    @Test
    public void cancel() {
        verifyBeforeCancel();
        assertTrue(future.cancel(false));
        assertFalse(future.cancel(false));
        verifyAfterCancel();
    }

    @Test
    public void cancelInterrupt() throws Exception {
        verifyBeforeCancel();
        assertTrue(future.cancel(true));
        assertFalse(future.cancel(true));
        resultAvailable.await(5, SECONDS);
        assertTrue(workerException instanceof InterruptedException);
        verifyAfterCancel();
    }

    @Test
    public void getWithTimeoutResultSetBeforeWaiting() throws Exception {
        startClientThread(() -> {
            clientThreadUp.countDown();
            future.done(update);
            resultAvailable.countDown();
        });
        clientThreadUp.await();
        assertSame(checksum, future.get(2000, MILLISECONDS));
        assertTrue(future.isDone());
    }

    @Test
    public void getWithTimeoutExceptionSetBeforeWaiting() throws Exception {
        final Exception expected = new Exception();
        when(update.getFailureOrNull()).thenReturn(expected);
        startClientThread(() -> {
            clientThreadUp.countDown();
            future.done(update);
            resultAvailable.countDown();
        });
        clientThreadUp.await();
        try {
            future.get(2000, MILLISECONDS);
            fail("Exception expected");
        } catch (final ExecutionException e) {
            assertSame(expected, e.getCause());
        }
        assertTrue(future.isDone());
    }

    @Test
    public void getWithTimeoutAwaitResult() throws Exception {
        startClientThread(() -> {
            clientThreadUp.countDown();
            try {
                sleep(1000);
            } catch (final InterruptedException e) {
                clientException = e;
            }
            future.done(update);
            resultAvailable.countDown();
        });
        clientThreadUp.await();
        assertSame(checksum, future.get(2000, MILLISECONDS));
        assertTrue(future.isDone());
    }

    @Test
    public void getWithTimeoutAwaitTimedOut() throws Exception {
        startClientThread(() -> {
            clientThreadUp.countDown();
            try {
                sleep(5000);
            } catch (final InterruptedException e) {
                clientException = e;
            }
            future.done(update);
            resultAvailable.countDown();
        });
        clientThreadUp.await();
        try {
            future.get(1000, MILLISECONDS);
            fail("Exception expected");
        } catch (final TimeoutException expected) {
            assertEquals("Timeout after 1000 MILLISECONDS", expected.getMessage());
        }
        assertFalse(future.isDone());
    }

    @Test
    public void getWithTimeoutExceptionOccurred() throws Exception {
        final Exception expected = new Exception();
        when(update.getFailureOrNull()).thenReturn(expected);
        startClientThread(() -> {
            clientThreadUp.countDown();
            try {
                sleep(1000);
            } catch (final InterruptedException e) {
                clientException = e;
            }
            future.done(update);
            resultAvailable.countDown();
        });
        clientThreadUp.await();
        try {
            future.get(2000, MILLISECONDS);
            fail("Exception expected");
        } catch (final ExecutionException e) {
            assertSame(expected, e.getCause());
        }
        assertTrue(future.isDone());
    }

    @Test
    public void getExceptionSetBeforeWait() throws Exception {
        final Exception expected = new Exception();
        when(update.getFailureOrNull()).thenReturn(expected);
        startClientThread(() -> {
            clientThreadUp.countDown();
            future.done(update);
            resultAvailable.countDown();
        });
        clientThreadUp.await();
        try {
            future.get();
            fail("Exception expected");
        } catch (final ExecutionException e) {
            assertSame(expected, e.getCause());
        }
        assertTrue(future.isDone());
    }

    @Test
    public void getResultSetBeforeWait() throws Exception {
        startClientThread(() -> {
            clientThreadUp.countDown();
            future.done(update);
            resultAvailable.countDown();
        });
        clientThreadUp.await();
        assertSame(checksum, future.get());
        assertTrue(future.isDone());
    }

    @Test
    public void get() throws Exception {
        startClientThread(() -> {
            clientThreadUp.countDown();
            try {
                sleep(1000);
            } catch (final InterruptedException e) {
                clientException = e;
            }
            future.done(update);
            resultAvailable.countDown();
        });
        clientThreadUp.await();
        assertSame(checksum, future.get());
        assertTrue(future.isDone());
    }

    @Test
    public void getExceptionOccurred() throws Exception {
        final Exception expected = new Exception();
        when(update.getFailureOrNull()).thenReturn(expected);
        startClientThread(() -> {
            clientThreadUp.countDown();
            try {
                sleep(1000);
            } catch (final InterruptedException e) {
                clientException = e;
            }
            future.done(update);
            resultAvailable.countDown();
        });
        clientThreadUp.await();
        try {
            future.get();
            fail("Exception expected");
        } catch (final ExecutionException e) {
            assertSame(expected, e.getCause());
        }
        assertTrue(future.isDone());
    }
}
