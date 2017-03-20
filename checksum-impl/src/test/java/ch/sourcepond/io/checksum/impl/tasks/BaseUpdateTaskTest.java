package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import java.io.IOException;
import java.security.MessageDigest;

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

        public TestUpdateTask(final DigesterPool pDigesterPool, final UpdateObserver pObserver, final BaseResource<StreamSource> pResource, final DataReader pReader) {
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

        @Override
        public void close() throws IOException {
            closed = true;
        }
    }

    private static final byte[] ANY_DATA = new byte[0];
    private final DataReader reader = mock(DataReader.class);
    private final DigesterPool digesterPool = mock(DigesterPool.class);
    private final MessageDigest digest = mock(MessageDigest.class);
    private final UpdateObserver observer = mock(UpdateObserver.class);
    private final BaseResource<StreamSource> resource = mock(BaseResource.class);
    private final Checksum checksum = mock(Checksum.class);
    private final TestUpdateTask task = new TestUpdateTask(digesterPool, observer, resource, reader);

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
    }

    @Test
    public void verifySuccess() throws Exception {
        when(resource.getCurrent()).thenReturn(checksum);
        task.call();
        final InOrder order = inOrder(digesterPool, digest, resource, observer);
        order.verify(digesterPool).get();
        order.verify(digest).update(ANY_DATA);
        order.verify(digest).digest();
        order.verify(resource).setCurrent(matchCurrent());
        order.verify(observer).done(argThat(u -> checksum.equals(u.getPrevious()) && "".equals(u.getCurrent().getHexValue())));
        order.verify(digesterPool).release(digest);
        assertTrue(task.closed);
    }

    @Test
    public void verifyCancel() throws Exception {
        task.interruptedException = new InterruptedException();
        task.call();
        final InOrder order = inOrder(digest, resource, observer, digesterPool);
        order.verify(digesterPool).get();
        order.verify(resource).getCurrent();
        order.verify(observer).done(argThat(u -> u.getFailureOrNull() == task.interruptedException));
        order.verify(digesterPool).release(digest);
        assertTrue(task.closed);
    }

    @Test
    public void verifyFailure() throws Exception {
        task.ioException = new IOException();
        task.call();
        final InOrder order = inOrder(digest, resource, observer, digesterPool);
        order.verify(digesterPool).get();
        order.verify(resource).getCurrent();
        order.verify(observer).done(argThat(u -> u.getFailureOrNull() == task.ioException));
        order.verify(digesterPool).release(digest);
        assertTrue(task.closed);
    }
}
