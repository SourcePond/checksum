package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class UpdateTaskTest<A> {
    private static final String EXPECTED_SHA_256_HASH = "b0a0a864cf2eb7c20a25bfe12f4cddc6070809e5da8f5da226234a258d17d336";
    final BaseResource<A> resource = mock(BaseResource.class);
    private final UpdateObserver observer = mock(UpdateObserver.class);
    private final CountDownLatch latch = new CountDownLatch(1);
    final ResultFuture future = new ResultFuture(observer);
    final ScheduledExecutorService executor = newScheduledThreadPool(1);
    final DigesterPool digesterPool = mock(DigesterPool.class);
    @SuppressWarnings("FieldCanBeLocal")
    private MessageDigest digest;
    private UpdateTask task;
    private volatile Checksum checksum;

    protected abstract UpdateTask newTask() throws IOException;

    @Before
    public void setup() throws Exception {
        digest = MessageDigest.getInstance("SHA-256");
        when(digesterPool.get()).thenReturn(digest);
        doAnswer(invocationOnMock -> {
                    checksum = invocationOnMock.getArgument(0);
                    latch.countDown();
                    return null;
                }
        ).when(resource).setCurrent(any());
        task = newTask();
        executor.execute(task);
        latch.await();
    }

    @After
    public void tearDown() {
        executor.shutdown();
    }

    @Test(timeout = 5000)
    public void verifyDigest() throws Exception {
        assertEquals(EXPECTED_SHA_256_HASH, checksum.getHexValue());
    }
}
