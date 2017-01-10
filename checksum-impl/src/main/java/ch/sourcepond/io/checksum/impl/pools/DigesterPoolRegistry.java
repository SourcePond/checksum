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

import ch.sourcepond.io.checksum.api.Algorithm;

import java.security.MessageDigest;
import java.util.EnumMap;
import java.util.Map;

import static ch.sourcepond.io.checksum.api.Algorithm.*;

/**
 * Factory to create new {@link DigesterPool} instances.
 */
public class DigesterPoolRegistry {
    private static final Map<Algorithm, DigesterPool> POOLS = new EnumMap<>(Algorithm.class);
    static {
        POOLS.put(MD2, new DigesterPool(MD2));
        POOLS.put(MD5, new DigesterPool(MD5));
        POOLS.put(SHA1, new DigesterPool(SHA1));
        POOLS.put(SHA224, new DigesterPool(SHA224));
        POOLS.put(SHA256, new DigesterPool(SHA256));
        POOLS.put(SHA384, new DigesterPool(SHA384));
        POOLS.put(SHA512, new DigesterPool(SHA512));
    }

    public Pool<MessageDigest> getPool(final Algorithm pAlgorithm) {
        return POOLS.get(pAlgorithm);
    }
}
