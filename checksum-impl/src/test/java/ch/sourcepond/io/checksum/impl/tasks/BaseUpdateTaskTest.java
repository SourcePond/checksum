package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.impl.ResourceContext;
import ch.sourcepond.io.checksum.impl.pools.Pool;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.security.MessageDigest;

import static org.mockito.Mockito.*;

/**
 * Created by rolandhauser on 09.01.17.
 */
public class BaseUpdateTaskTest {

    private static class TestUpdateTask extends UpdateTask {
        CancelException cancelException;
        IOException ioException;

        public TestUpdateTask(final ResourceContext pResource, final DataReader pAvailability) {
            super(pResource, pAvailability);
        }

        @Override
        void updateDigest(final MessageDigest pDigest) throws IOException, CancelException {
            if (cancelException != null) {
                throw cancelException;
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
    private final ResourceContext resource = mock(ResourceContext.class);
    private final Checksum checksum = mock(Checksum.class);
    private final TestUpdateTask task = new TestUpdateTask(resource, reader);

    @Before
    public void setup() {
        when(resource.getDigesterPool()).thenReturn(digesterPool);
        when(digesterPool.get()).thenReturn(digest);
        when(digest.digest()).thenReturn(ANY_DATA);
        when(resource.newChecksum(ANY_DATA)).thenReturn(checksum);
    }

    @Test
    public void verifySuccess() {
        task.run();
        final InOrder order = inOrder(digesterPool, digest, resource);
        order.verify(digesterPool).get();
        order.verify(digest).update(ANY_DATA);
        order.verify(digest).digest();
        order.verify(resource).informSuccessObservers(checksum);
        order.verify(digesterPool).release(digest);
    }

    @Test
    public void verifyCancel() {
        task.cancelException = new CancelException();
        task.run();
        final InOrder order = inOrder(digest, resource, digesterPool);
        order.verify(digesterPool).get();
        order.verify(resource).informCancelObservers();
        order.verify(digesterPool).release(digest);
    }

    @Test
    public void verifyFailure() {
        task.ioException = new IOException();
        task.run();
        final InOrder order = inOrder(digest, resource, digesterPool);
        order.verify(digesterPool).get();
        order.verify(resource).informFailureObservers(task.ioException);
        order.verify(digesterPool).release(digest);
    }

    @Test
    public void cancel() throws Exception {
        task.cancel();
        verify(reader).cancel();
    }
}
