package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.Checksum;

import java.io.IOException;

/**
 * Created by rolandhauser on 06.01.17.
 */
public interface Observable<S, A> {

    S getSource();

    A getAccessor();

    void informCancelObservers();

    void informSuccessObservers(Checksum pChecksum);

    void informFailureObservers(IOException e);
}
