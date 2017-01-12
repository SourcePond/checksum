package ch.sourcepond.io.checksum.impl.store;

import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import org.junit.Test;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static org.junit.Assert.*;

/**
 * Created by rolandhauser on 12.01.17.
 */
public class ResourceMapTest {
    private final ResourceMap map = new ResourceMap(SHA256);

    @Test
    public void getDigesterPool() {
       final DigesterPool pool = map.getPool();
        assertNotNull(pool);
        assertEquals(SHA256, pool.getAlgorithm());
    }
}
