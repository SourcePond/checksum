package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.impl.pools.BufferPool;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.Observable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 *
 */
@SuppressWarnings({"unchecked", "FieldCanBeLocal"})
public class TaskFactoryTest {
    private final DigesterPool digesterPool = mock(DigesterPool.class);
    private final BufferPool bufferPool = mock(BufferPool.class);
    private final TaskFactory factory = new TaskFactory(bufferPool);
    private final Observable resource = mock(Observable.class);
    private URL anyUrl;

    @Before
    public void setup() throws Exception {
        anyUrl = new URL("file:///any/path");
    }

    private void assertNotSame(final Callable<Checksum> first, final Callable<Checksum> second) {
        assertNotNull(first);
        assertNotNull(second);
        Assert.assertNotSame(first, second);
    }

    @Test
    public void verifyFactoryMethods() {
        assertNotSame(factory.newChannelTask(digesterPool, resource, TimeUnit.SECONDS, 1L),
                factory.newChannelTask(digesterPool, resource, TimeUnit.SECONDS, 1L));
        assertNotSame(factory.newStreamTask(digesterPool, resource, TimeUnit.SECONDS, 1L),
                factory.newStreamTask(digesterPool, resource, TimeUnit.SECONDS, 1L));
    }
}
