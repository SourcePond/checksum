/*Copyright (C) 2017 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.io.checksum.impl.store;

import ch.sourcepond.io.checksum.api.Resource;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ResourceStoreTest {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final StreamSource source = mock(StreamSource.class);
    private final ResourceSupplier<StreamSource> supplier = mock(ResourceSupplier.class);
    private final Resource<StreamSource> resource = mock(Resource.class);
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
        reset(resource);
        final Resource<StreamSource> res2 = store.get(SHA256, source, supplier);
        assertNotNull(res2);
        assertSame(res1, res2);
    }


    @Test
    public void ignoreNewResourceIfAnotherIsAlreadyRegistered() throws Exception {
        final Resource<StreamSource> toBeIgnored = mock(Resource.class);
        final CountDownLatch latch = new CountDownLatch(2);
        executor.submit(() -> {
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
        );
        executor.schedule(() -> {
                    store.get(SHA256, source, pool -> resource);
                    latch.countDown();
                    return null;
                }
                , 100L, MILLISECONDS);
        latch.await();
        assertSame(resource, store.get(SHA256, source, supplier));
    }
}
