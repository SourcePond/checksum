package ch.sourcepond.io.checksum.impl.store;

import ch.sourcepond.io.checksum.api.Resource;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.LeasableResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by rolandhauser on 12.01.17.
 */
public class ResourceStoreTest {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final StreamSource source = mock(StreamSource.class);
    private final ResourceSupplier<StreamSource> supplier = mock(ResourceSupplier.class);
    private final LeasableResource<StreamSource> resource = mock(LeasableResource.class);
    private ResourceStore store;
    private DigesterPool pool;

    @Before
    public void setup() {
        store = new ResourceStore();
        pool = store.resources.get(SHA256).getPool();
        when(supplier.supply(pool)).thenReturn(resource);
    }

    @After
    public void tearDown() {
        executor.shutdown();
    }

    @Test
    public void verifySameInstance() {
        final Resource<StreamSource> res1 = store.get(SHA256, source, supplier);
        assertNotNull(res1);
        verify(resource).lease();
        reset(resource);
        final Resource<StreamSource> res2 = store.get(SHA256, source, supplier);
        assertNotNull(res2);
        assertSame(res1, res2);
        verify(resource).lease();
    }

    @Test
    public void dispose() {
        final LeasableResource<StreamSource> res1 = mock(LeasableResource.class);
        final LeasableResource<StreamSource> res2 = mock(LeasableResource.class);
        when(res1.getAlgorithm()).thenReturn(SHA256);
        when(res2.getAlgorithm()).thenReturn(SHA256);
        when(res1.getSource()).thenReturn(source);
        when(res2.getSource()).thenReturn(source);
        when(supplier.supply(pool)).thenReturn(res1).thenReturn(res2);
        assertSame(res1, store.get(SHA256, source, supplier));
        store.dispose(res1);
        assertSame(res2, store.get(SHA256, source, supplier));
    }

    @Test
    public void ignoreNewResourceIfAnotherIsAlreadyRegistered() throws Exception {
        final LeasableResource<StreamSource> toBeIgnored = mock(LeasableResource.class);
        final CountDownLatch latch = new CountDownLatch(2);
        executor.submit(new Callable<Resource<StreamSource>>() {

            @Override
            public Resource<StreamSource> call() throws Exception {
                store.get(SHA256, source, pool -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return toBeIgnored;
                });
                latch.countDown();
                return null;
            }
        });
        executor.schedule(new Callable<Resource<StreamSource>>() {
            @Override
            public Resource<StreamSource> call() throws Exception {
                store.get(SHA256, source, pool -> resource);
                latch.countDown();
                return null;
            }
        }, 100L, MILLISECONDS);
        latch.await();
        assertSame(resource, store.get(SHA256, source, supplier));
    }
}
