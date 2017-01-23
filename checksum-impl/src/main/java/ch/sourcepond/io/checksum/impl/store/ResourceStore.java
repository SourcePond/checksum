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
package ch.sourcepond.io.checksum.impl.store;

import ch.sourcepond.io.checksum.api.Algorithm;
import ch.sourcepond.io.checksum.api.Resource;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ResourceStore {
    final Map<Algorithm, ResourceMap> resources = new EnumMap<>(Algorithm.class);

    public ResourceStore() {
        for (final Algorithm algorithm : Algorithm.values()) {
            resources.put(algorithm, new ResourceMap(algorithm));
        }
    }

    public <T> Resource<T> get(final Algorithm pAlgorithm, T pSource, ResourceSupplier pSupplier) {
        final ResourceMap map = resources.get(pAlgorithm);
        Resource<?> rc = map.get(pSource);
        if (null == rc) {
            rc = pSupplier.supply(map.getPool());
            Resource<?> current = map.putIfAbsent(pSource, rc);

            if (null != current) {
                rc = current;
            }
        }
        return (Resource<T>) rc;
    }
}
