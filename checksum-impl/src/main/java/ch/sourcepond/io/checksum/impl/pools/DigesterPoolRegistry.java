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

import java.util.EnumMap;

/**
 *
 */
public class DigesterPoolRegistry {
    private final EnumMap<Algorithm, DigesterPool> digesterPools = new EnumMap<>(Algorithm.class);

    public DigesterPoolRegistry() {
        for (final Algorithm algorithm : Algorithm.values()) {
            digesterPools.put(algorithm, new DigesterPool(algorithm));
        }
    }

    public DigesterPool get(final Algorithm pAlgorithm) {
        return digesterPools.get(pAlgorithm);
    }
}
