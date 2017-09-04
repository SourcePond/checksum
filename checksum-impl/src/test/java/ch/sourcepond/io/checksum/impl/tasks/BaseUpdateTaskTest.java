package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class BaseUpdateTaskTest {

    private static class TestUpdateTask extends UpdateTask<StreamSource> {
        InterruptedException interruptedException;
        IOException ioException;
        boolean closed;
        boolean delay;

        public TestUpdateTask(final ScheduledExecutorService pExecutor,
                              final ResultFuture pFuture,
                              final DigesterPool pDigesterPool,
                              final BaseResource<StreamSource> pResource,
                              final TimeUnit pUnit,
                              final long pDelay) {
            super(pExecutor, pFuture, pDigesterPool, pResource, pUnit, pDelay);
        }

        @Override
        boolean updateDigest() throws InterruptedException, IOException {
            if (interruptedException != null) {
                throw interruptedException;
            }
            if (ioException != null) {
                throw ioException;
            }
            digest.update(ANY_DATA);
            return delay;
        }

        @Override
        public void close() throws IOException {
            closed = true;
        }
    }

    private static final byte[] ANY_DATA = new byte[0];
    private final Checksum initialChecksum = mock(Checksum.class);
    private final ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
    private final DigesterPool digesterPool = mock(DigesterPool.class);
    private final MessageDigest digest = mock(MessageDigest.class);
    private final BaseResource<StreamSource> resource = mock(BaseResource.class);
    private final Checksum checksum = mock(Checksum.class);
    private final ResultFuture future = mock(ResultFuture.class);
    private TestUpdateTask task;

    private static Checksum matchCurrent() {
        return argThat(new ArgumentMatcher<Checksum>() {
            @Override
            public boolean matches(final Checksum checksum) {
                return "".equals(checksum.getHexValue());
            }
        });
    }

    @Before
    public void setup() {
        when(digesterPool.get()).thenReturn(digest);
        when(digest.digest()).thenReturn(ANY_DATA);
        when(resource.getCurrent()).thenReturn(initialChecksum);
        task = new TestUpdateTask(executor, future, digesterPool, resource, SECONDS, 1);
    }

    @Test
    public void verifyDelayExecution() throws Exception {
        when(resource.getCurrent()).thenReturn(checksum);
        task.delay = true;
        task.run();
        final InOrder order = inOrder(digesterPool, digest, executor, resource, future);
        order.verify(digesterPool).get();
        order.verify(digest).update(ANY_DATA);
        order.verify(executor).schedule(task, 1, SECONDS);
        order.verifyNoMoreInteractions();
    }

    @Test
    public void verifySuccess() throws Exception {
        when(resource.getCurrent()).thenReturn(checksum);
        task.run();
        final InOrder order = inOrder(digesterPool, digest, resource, future);
        order.verify(digesterPool).get();
        order.verify(digest).update(ANY_DATA);
        order.verify(digest).digest();
        order.verify(resource).finalizeUpdate(matchCurrent());
        order.verify(future).done(argThat(u -> initialChecksum.equals(u.getPrevious()) && "".equals(u.getCurrent().getHexValue())));
        order.verify(digesterPool).release(digest);
        verifyZeroInteractions(executor);
        assertTrue(task.closed);
    }

    @Test
    public void verifyCancel() throws Exception {
        task.interruptedException = new InterruptedException();
        task.run();
        final InOrder order = inOrder(digest, resource, future, digesterPool);
        order.verify(digesterPool).get();
        order.verify(resource).getCurrent();
        order.verify(future).done(argThat(u -> u.getFailureOrNull() == task.interruptedException));
        order.verify(digesterPool).release(digest);
        assertTrue(task.closed);
    }

    @Test
    public void verifyFailure() throws Exception {
        task.ioException = new IOException();
        task.run();
        final InOrder order = inOrder(digest, resource, future, digesterPool);
        order.verify(digesterPool).get();
        order.verify(resource).getCurrent();
        order.verify(future).done(argThat(u -> u.getFailureOrNull() == task.ioException));
        order.verify(digesterPool).release(digest);
        assertTrue(task.closed);
    }
}
