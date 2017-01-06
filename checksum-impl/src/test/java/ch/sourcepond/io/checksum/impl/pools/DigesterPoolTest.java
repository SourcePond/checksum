package ch.sourcepond.io.checksum.impl.pools;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by rolandhauser on 06.01.17.
 */
public class DigesterPoolTest extends BasePoolTest<DigesterPool> {
    private final byte[] EXPECTED_CLEAN_BYTES = new byte[]{
            -29, -80, -60, 66, -104, -4, 28, 20,
            -102, -5, -12, -56, -103, 111, -71, 36,
            39, -82, 65, -28, 100, -101, -109, 76,
            -92, -107, -103, 27, 120, 82, -72, 85
    };
    private final DigesterPoolFactory poolFactory = new DigesterPoolFactory();

    @Override
    protected DigesterPool newTestPool() throws Exception {
        return poolFactory.newPool("SHA-256");
    }

    @Test
    public void resetDigestAfterRelease() {
        MessageDigest digest = pool.get();

        // Update digest without actually digest the data
        digest.update(new byte[]{2, 3, 4, 5, 6});
        pool.release(digest);
        assertArrayEquals(EXPECTED_CLEAN_BYTES, digest.digest());
    }

    @Test
    public void verifyImpossibleCaseThatAlgorithmIsInvalid() throws Exception {
        final Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        final Unsafe unsafe = (Unsafe)f.get(Unsafe.class);

        Field algorithmField = DigesterPool.class.getDeclaredField("algorithm");
        algorithmField.setAccessible(true);
        final DigesterPool pool = (DigesterPool) unsafe.allocateInstance(DigesterPool.class);
        algorithmField.set(pool, "UNKNOWN");

        try {
            pool.newPooledObject();
            fail("Exception expected here");
        } catch (final IllegalStateException e) {
            assertSame(NoSuchAlgorithmException.class, e.getCause().getClass());
        }
    }
}
