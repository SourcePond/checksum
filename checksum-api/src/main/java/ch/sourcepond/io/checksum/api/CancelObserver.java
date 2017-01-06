package ch.sourcepond.io.checksum.api;

/**
 * Created by roland on 04.01.17.
 */
public interface CancelObserver<T> {

    void updateCancelled(T pSource);
}
