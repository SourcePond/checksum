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
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.LeasableResource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe store for {@link LeasableResource} instances.
 */
final class ResourceMap {
    private final ConcurrentMap<Object, LeasableResource<?>> map = new ConcurrentHashMap<>();
    private final DigesterPool pool;

    ResourceMap(final Algorithm pAlgorithm) {
        pool = new DigesterPool(pAlgorithm);
    }

    DigesterPool getPool() {
        return pool;
    }

    void remove(final Object pSource) {
        map.remove(pSource);
    }

    @SuppressWarnings("unchecked")
    <T> LeasableResource<T> get(final T pSource) {
        return (LeasableResource<T>)map.get(pSource);
    }

    @SuppressWarnings("unchecked")
    <T> LeasableResource<T> putIfAbsent(final T pSource, final LeasableResource<?> pValue) {
        return (LeasableResource<T>)map.putIfAbsent(pSource, pValue);
    }
}
