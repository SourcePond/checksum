package ch.sourcepond.io.checksum.api;

/**
 * Created by roland on 04.01.17.
 */
public interface UpdateSuccessObserver<T> extends UpdateObserver<T> {

    void updateSucceeded(T pSource, Checksum pPrevious, Checksum pCurrent);
}
