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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Pool for caching {@link MessageDigest} instances.
 */
final class DigesterPool extends BasePool<MessageDigest> {
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
