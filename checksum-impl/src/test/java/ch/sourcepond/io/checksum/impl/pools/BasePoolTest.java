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
package ch.sourcepond.io.checksum.impl.pools;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public abstract class BasePoolTest<T> {
    BasePool<T> pool;

    @Before
    public void setup() throws Exception {
        pool = newTestPool();
    }

    protected abstract BasePool<T> newTestPool();

    @Test(expected = NullPointerException.class)
    public void nullPointerIfObjectToBeReleasedIsNull() {
        pool.release(null);
    }

    @Test
    public void verifyGetAndRelease() {
        final T pooledObject = pool.get();
        assertNotNull(pooledObject);
        pool.release(pooledObject);
        assertSame(pooledObject, pool.get());
    }

    @SuppressWarnings("UnusedAssignment")
    @Test
    public void verifyGetAndReleaseWithGC() throws Exception {
        T pooledObject = pool.get();
        assertNotNull(pooledObject);
        final int pooledObjectHashCode = System.identityHashCode(pooledObject);
        pool.release(pooledObject);
        pooledObject = null;
        System.gc();

        // Ugly, but necessary: give GC enough time to deliver WeakReference to
        // reference queue.
        Thread.sleep(1000);

        pooledObject = pool.get();
        assertNotNull(pooledObject);
        assertNotEquals(pooledObjectHashCode, System.identityHashCode(pooledObject));
    }
}
