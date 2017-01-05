package ch.sourcepond.io.checksum.api;

/**
 * Created by roland on 04.01.17.
 */
public interface UpdateFailureObserver extends UpdateObserver {

    void updateFailed(ChecksumException pException);
}
