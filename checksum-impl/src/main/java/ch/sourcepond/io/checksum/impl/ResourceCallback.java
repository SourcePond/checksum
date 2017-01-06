package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.impl.pools.BufferPool;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;

import java.io.IOException;

/**
 * Created by rolandhauser on 06.01.17.
 */
public interface ResourceCallback {

    DigesterPool getDigesterPool();

    BufferPool getBufferPool();

    void informCancelObservers();

    void informSuccessObservers(Checksum pChecksum);

    void informFailureObservers(IOException e);
}
