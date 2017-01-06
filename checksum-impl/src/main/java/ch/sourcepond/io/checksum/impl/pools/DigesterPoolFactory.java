package ch.sourcepond.io.checksum.impl.pools;

import java.security.NoSuchAlgorithmException;

/**
 * Created by rolandhauser on 06.01.17.
 */
public class DigesterPoolFactory {

    public DigesterPool newPool(final String pAlgorithm) throws NoSuchAlgorithmException {
        return new DigesterPool(pAlgorithm);
    }
}
