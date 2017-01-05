package ch.sourcepond.io.checksum.api;

/**
 * Created by roland on 04.01.17.
 */
public interface UpdateCancelObserver extends UpdateObserver {

    void updateSucceeded(Checksum pChecksum);
}
