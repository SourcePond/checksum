package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.impl.ChecksumImpl;
import ch.sourcepond.io.checksum.impl.ResourceContext;
import ch.sourcepond.io.checksum.impl.pools.Pool;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by rolandhauser on 09.01.17.
 */
public abstract class UpdateTaskTest {
    private static final String EXPECTED_SHA_256_HASH = "b0a0a864cf2eb7c20a25bfe12f4cddc6070809e5da8f5da226234a258d17d336";
    protected final DataReader reader = new DataReader(SECONDS, 1);
    protected final ResourceContext resource = mock(ResourceContext.class);
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Pool<MessageDigest> digesterPool = mock(Pool.class);
    private MessageDigest digest;
    protected UpdateTask task;
    private volatile Checksum checksum;

    protected abstract UpdateTask newTask();

    @Before
    public void setup() throws Exception {
        digest = MessageDigest.getInstance("SHA-256");
        when(resource.getDigesterPool()).thenReturn(digesterPool);
        when(digesterPool.get()).thenReturn(digest);
        doAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                checksum = invocationOnMock.getArgument(0);
                return null;
            }
        }).when(resource).informSuccessObservers(any());
        when(resource.newChecksum(Mockito.any())).thenAnswer(new Answer<Checksum>() {
            @Override
            public Checksum answer(final InvocationOnMock invocationOnMock) throws Throwable {
                return new ChecksumImpl(invocationOnMock.getArgument(0));
            }
        });
        task = newTask();
    }

    @Test(timeout = 3000)
    public void verifyDigest() throws Exception {
        executor.submit(task).get();
        assertEquals(EXPECTED_SHA_256_HASH, checksum.getHexValue());
    }

    @Test(timeout = 3000)
    public void verifyCancel() throws Exception {
        final Future<?> future = executor.submit(task);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                task.cancel();
            }
        });
        future.get();
        verify(resource).informCancelObservers();
    }
}
