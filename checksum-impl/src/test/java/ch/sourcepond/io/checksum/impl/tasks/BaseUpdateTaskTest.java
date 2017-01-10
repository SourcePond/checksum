package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.impl.resources.Observable;
import ch.sourcepond.io.checksum.impl.pools.Pool;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.security.MessageDigest;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

/**
 * Created by rolandhauser on 09.01.17.
 */
public class BaseUpdateTaskTest {

    private static class TestUpdateTask extends UpdateTask {
        InterruptedException interruptedException;
        IOException ioException;

        public TestUpdateTask(final Pool<MessageDigest> pDigesterPool, final Observable pResource, final DataReader pReader) {
            super(pDigesterPool, pResource, pReader);
        }

        @Override
        void updateDigest(final MessageDigest pDigest) throws InterruptedException, IOException {
            if (interruptedException != null) {
                throw interruptedException;
            }
            if (ioException != null) {
                throw ioException;
            }
            pDigest.update(ANY_DATA);
        }
    }

    private static final byte[] ANY_DATA = new byte[0];
    private final DataReader reader = mock(DataReader.class);
    private final Pool<MessageDigest> digesterPool = mock(Pool.class);
    private final MessageDigest digest = mock(MessageDigest.class);
    private final Observable resource = mock(Observable.class);
    private final Checksum checksum = mock(Checksum.class);
    private final TestUpdateTask task = new TestUpdateTask(digesterPool, resource, reader);

    @Before
    public void setup() {
        when(digesterPool.get()).thenReturn(digest);
        when(digest.digest()).thenReturn(ANY_DATA);
    }

    @Test
    public void verifySuccess() throws Exception {
        task.call();
        final InOrder order = inOrder(digesterPool, digest, resource);
        order.verify(digesterPool).get();
        order.verify(digest).update(ANY_DATA);
        order.verify(digest).digest();
        order.verify(resource).informSuccessObservers(notNull());
        order.verify(digesterPool).release(digest);
    }

    @Test
    public void verifyCancel() throws Exception {
        task.interruptedException = new InterruptedException();
        try {
            task.call();
            fail("Exception expected");
        } catch (final InterruptedException e) {
            // Noop
        }
        final InOrder order = inOrder(digest, resource, digesterPool);
        order.verify(digesterPool).get();
        order.verify(resource).informCancelObservers();
        order.verify(digesterPool).release(digest);
    }

    @Test
    public void verifyFailure() throws Exception {
        task.ioException = new IOException();
        try {
            task.call();
            fail("Exception expected");
        } catch (final IOException e) {
            // Noop
        }
        final InOrder order = inOrder(digest, resource, digesterPool);
        order.verify(digesterPool).get();
        order.verify(resource).informFailureObservers(task.ioException);
        order.verify(digesterPool).release(digest);
    }
}
