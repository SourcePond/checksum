package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.CalculationObserver;
import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.security.MessageDigest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class BaseUpdateTaskTest {

    private static class TestUpdateTask extends UpdateTask<StreamSource> {
        InterruptedException interruptedException;
        IOException ioException;

        public TestUpdateTask(final DigesterPool pDigesterPool, final CalculationObserver pObserver, final BaseResource<StreamSource> pResource, final DataReader pReader) {
            super(pDigesterPool, pObserver, pResource, pReader);
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
    private final DigesterPool digesterPool = mock(DigesterPool.class);
    private final MessageDigest digest = mock(MessageDigest.class);
    private final CalculationObserver observer = mock(CalculationObserver.class);
    private final BaseResource<StreamSource> resource = mock(BaseResource.class);
    private final Checksum checksum = mock(Checksum.class);
    private final TestUpdateTask task = new TestUpdateTask(digesterPool, observer, resource, reader);

    @Before
    public void setup() {
        when(digesterPool.get()).thenReturn(digest);
        when(digest.digest()).thenReturn(ANY_DATA);
        when(resource.updateChecksum(notNull())).thenReturn(checksum);
    }

    @Test
    public void verifySuccess() throws Exception {
        task.call();
        final InOrder order = inOrder(digesterPool, digest, observer);
        order.verify(digesterPool).get();
        order.verify(digest).update(ANY_DATA);
        order.verify(digest).digest();
        order.verify(observer).done(same(checksum), notNull());
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
        order.verify(digesterPool).release(digest);
    }
}
