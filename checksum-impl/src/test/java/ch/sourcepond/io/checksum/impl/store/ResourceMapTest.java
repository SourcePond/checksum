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

import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.LeasableResource;
import org.junit.Test;

import java.nio.file.Path;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by rolandhauser on 12.01.17.
 */
public class ResourceMapTest {
    private final Path key = mock(Path.class);
    private final LeasableResource<Path> value = mock(LeasableResource.class);
    private final ResourceMap map = new ResourceMap(SHA256);

    @Test
    public void getDigesterPool() {
        final DigesterPool pool = map.getPool();
        assertNotNull(pool);
        assertEquals(SHA256, pool.getAlgorithm());
    }

    @Test
    public void checkPufIfAbsentGetAndRemove() {
        assertNull(map.putIfAbsent(key, value));
        final LeasableResource<Path> secondValue = mock(LeasableResource.class);
        assertSame(value, map.putIfAbsent(key, secondValue));
        assertSame(value, map.get(key));
        map.remove(key);
        assertNull(map.get(key));
    }
}
