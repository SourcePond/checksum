package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.CalculationObserver;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;
import org.junit.Before;
import org.junit.Test;

import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class UpdateTaskTest<A> {
    private static final String EXPECTED_SHA_256_HASH = "b0a0a864cf2eb7c20a25bfe12f4cddc6070809e5da8f5da226234a258d17d336";
    final DataReader reader = new DataReader(SECONDS, 1);
    final CalculationObserver observer = mock(CalculationObserver.class);
    final BaseResource<A> resource = mock(BaseResource.class);
    private final ExecutorService executor = Executors.newCachedThreadPool();
    final DigesterPool digesterPool = mock(DigesterPool.class);
    @SuppressWarnings("FieldCanBeLocal")
    private MessageDigest digest;
    private UpdateTask task;
    private volatile Checksum checksum;

    protected abstract UpdateTask newTask();

    @Before
    public void setup() throws Exception {
        digest = MessageDigest.getInstance("SHA-256");
        when(digesterPool.get()).thenReturn(digest);
        doAnswer(invocationOnMock -> {
                    checksum = invocationOnMock.getArgument(0);
                    return null;
                }
        ).when(resource).updateChecksum(any());
        task = newTask();
    }

    @Test(timeout = 6000)
    public void verifyDigest() throws Exception {
        executor.submit(task).get();
        assertEquals(EXPECTED_SHA_256_HASH, checksum.getHexValue());
    }
}
