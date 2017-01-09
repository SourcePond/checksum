package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.impl.pools.Pool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Created by rolandhauser on 06.01.17.
 */
public interface ResourceContext {

    Pool<MessageDigest> getDigesterPool();

    Pool<ByteBuffer> getBufferPool();


    /**
     * Create a new {@link Checksum} instance.
     *
     * @param pDigest The data represented by the new checksum object.
     * @return New checksum object, never {@code null}.
     */
    Checksum newChecksum(byte[] pDigest);

    void informCancelObservers();

    void informSuccessObservers(Checksum pChecksum);

    void informFailureObservers(IOException e);
}
