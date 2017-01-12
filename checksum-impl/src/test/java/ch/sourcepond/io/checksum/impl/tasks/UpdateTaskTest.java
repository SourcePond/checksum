package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.Observable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by rolandhauser on 09.01.17.
 */
public abstract class UpdateTaskTest<S, A> {
    private static final String EXPECTED_SHA_256_HASH = "b0a0a864cf2eb7c20a25bfe12f4cddc6070809e5da8f5da226234a258d17d336";
    protected final DataReader reader = new DataReader(SECONDS, 1);
    protected final Observable<S, A> resource = mock(Observable.class);
    private final ExecutorService executor = Executors.newCachedThreadPool();
    protected final DigesterPool digesterPool = mock(DigesterPool.class);
    private MessageDigest digest;
    protected UpdateTask task;
    private volatile Checksum checksum;

    protected abstract UpdateTask newTask();

    @Before
    public void setup() throws Exception {
        digest = MessageDigest.getInstance("SHA-256");
        when(digesterPool.get()).thenReturn(digest);
        doAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                checksum = invocationOnMock.getArgument(0);
                return null;
            }
        }).when(resource).informSuccessObservers(any());
        task = newTask();
    }

    @Test(timeout = 6000)
    public void verifyDigest() throws Exception {
        executor.submit(task).get();
        assertEquals(EXPECTED_SHA_256_HASH, checksum.getHexValue());
    }
}
