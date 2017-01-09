package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.ResourceContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by rolandhauser on 09.01.17.
 */
public class TaskFactoryTest {
    private final TaskFactory factory = new TaskFactory();
    private final ResourceContext resource = mock(ResourceContext.class);
    private URL anyUrl;

    @Before
    public void setup() throws Exception {
        anyUrl = new URL("file:///any/path");
    }

    private void assertNotSame(final Runnable first, final Runnable second) {
        assertNotNull(first);
        assertNotNull(second);
        Assert.assertNotSame(first, second);
    }

    @Test
    public void verifyFactoryMethods() {
        assertNotSame(factory.newChannelTask(resource, TimeUnit.SECONDS, 1L, mock(ChannelSource.class)),
                factory.newChannelTask(resource, TimeUnit.SECONDS, 1L, mock(ChannelSource.class)));
        assertNotSame(factory.newFileTask(resource, TimeUnit.SECONDS, 1L, mock(Path.class)),
                factory.newFileTask(resource, TimeUnit.SECONDS, 1L, mock(Path.class)));
        assertNotSame(factory.newStreamTask(resource, TimeUnit.SECONDS, 1L, mock(StreamSource.class)),
                factory.newStreamTask(resource, TimeUnit.SECONDS, 1L, mock(StreamSource.class)));
        assertNotSame(factory.newURLTask(resource, TimeUnit.SECONDS, 1L, anyUrl),
                factory.newURLTask(resource, TimeUnit.SECONDS, 1L, anyUrl));
    }
}
