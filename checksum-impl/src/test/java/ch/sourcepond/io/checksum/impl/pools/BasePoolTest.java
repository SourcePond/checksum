package ch.sourcepond.io.checksum.impl.pools;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rolandhauser on 06.01.17.
 */
public abstract class BasePoolTest<T extends BasePool> {
    protected T pool;

    @Before
    public void setup() throws Exception {
        pool = newTestPool();
    }

    protected abstract T newTestPool() throws Exception;

    @Test
    public void verifyGetAndRelease() {
        final Object pooledObject = pool.get();
        assertNotNull(pooledObject);
        pool.release(pooledObject);
        assertSame(pooledObject, pool.get());
    }

    @Test
    public void verifyGetAndReleaseWithGC() throws Exception {
        Object pooledObject = pool.get();
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
