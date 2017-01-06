package ch.sourcepond.io.checksum.impl.pools;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Created by rolandhauser on 06.01.17.
 */
abstract class BasePool<T> {
    private final ReferenceQueue<T> clearedReferences = new ReferenceQueue<>();
    private final LinkedList<WeakReference<T>> pool = new LinkedList<>();

    abstract T newPooledObject();

    abstract void pooledObjectReleased(T pPooledObject);

    void addToPool(final T pPooledObject) {
        pool.add(new WeakReference<T>(pPooledObject, clearedReferences));
    }

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

    public final synchronized void release(final T pPooledObject) {
        pooledObjectReleased(pPooledObject);
        addToPool(pPooledObject);
    }
}
