package ch.sourcepond.io.checksum.api;

import java.io.IOException;

/**
 * Created by roland on 04.01.17.
 */
public interface UpdateFailureObserver<T> extends UpdateObserver<T> {

    void updateFailed(T pSource, IOException pFailure);
}
