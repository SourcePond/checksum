package ch.sourcepond.io.checksum.impl.pools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by rolandhauser on 06.01.17.
 */
public class DigesterPool extends BasePool<MessageDigest> {
    private final String algorithm;

    DigesterPool(final String pAlgorithm) throws NoSuchAlgorithmException {
        // First of all, create and add a new MessageDigest; this insures that the
        // algorithm specified is valid. Otherwise, an IllegalArgumentException will
        // be caused to be thrown.
        addToPool(MessageDigest.getInstance(pAlgorithm));
        algorithm = pAlgorithm;
    }

    @Override
    MessageDigest newPooledObject() {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            // This can never happen because it's already validated that the algorithm is valid
            // (was done during construction of this object).
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    void pooledObjectReleased(final MessageDigest pPooledObject) {
        // In any case reset the digest
        pPooledObject.reset();
    }
}
