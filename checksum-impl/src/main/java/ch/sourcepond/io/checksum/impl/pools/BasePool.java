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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Base implementation of the {@link Pool} interface.
 */
abstract class BasePool<T> implements Pool<T> {
    private final ReferenceQueue<T> clearedReferences = new ReferenceQueue<>();
    private final LinkedList<WeakReference<T>> pool = new LinkedList<>();

    abstract T newPooledObject();

    abstract void pooledObjectReleased(T pPooledObject);

    void addToPool(final T pPooledObject) {
        pool.add(new WeakReference<T>(pPooledObject, clearedReferences));
    }

    @Override
    public final synchronized T get() {
        Reference<?> ref = clearedReferences.poll();
        while (ref != null) {
            pool.remove(ref);
            ref = clearedReferences.poll();
        }

        T pooledObject = null;
        if (!pool.isEmpty()) {
            pooledObject = pool.removeFirst().get();
        }

        if (null == pooledObject) {
            pooledObject = newPooledObject();
        }

        return pooledObject;
    }

    @Override
    public final synchronized void release(final T pPooledObject) {
        pooledObjectReleased(pPooledObject);
        addToPool(pPooledObject);
    }
}
