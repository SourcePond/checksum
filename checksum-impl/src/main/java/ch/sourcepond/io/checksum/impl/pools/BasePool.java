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
 * Base pool implementation. This pool stores objects withing an {@link LinkedList}. When
 * an object is requested, the first object is removed from the list an returned. Clients of this
 * class should ensure that leased objects will be put back after usage, otherwise,
 * a serious performance impact could rise.
 */
abstract class BasePool<T> {
    private final ReferenceQueue<T> clearedReferences = new ReferenceQueue<>();
    private final LinkedList<WeakReference<? extends T>> pool = new LinkedList<>();

    /**
     * Creates a new pooled object.
     *
     * @return New object, never {@code null}
     */
    abstract T newPooledObject();

    /**
     * Indicates that an object has been released through {@link #release(Object)}. This
     * method is called <em>before</em> the object is re-added to the pool.
     *
     * @param pPooledObject Release object, never {@code null}
     */
    abstract void pooledObjectReleased(T pPooledObject);

    private void addToPool(final T pPooledObject) {
        pool.add(new WeakReference<>(pPooledObject, clearedReferences));
    }

    /**
     * Removes the first item from the pool and returns it. If the pool is empty, a
     * new object will be created and returned. Ensure that you release the object
     * returned by this method with {@link #release(Object)} after usage.
     *
     * @return Pooled object, never {@code null}
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public synchronized T get() {
        Reference<? extends T> ref = clearedReferences.poll();
        while (null != ref) {
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

    /**
     * Re-adds the object specified to the pool.
     *
     * @param pPooledObject Object to release, must not be {@code null}
     * @throws NullPointerException Thrown if the object specified is {@code null}.
     */
    public synchronized void release(final T pPooledObject) {
        if (pPooledObject == null) {
            throw new NullPointerException("Object to be released is null");
        }
        pooledObjectReleased(pPooledObject);
        addToPool(pPooledObject);
    }
}
