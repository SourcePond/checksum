package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.Checksum;

import java.io.IOException;

/**
 *
 */
final class ObserversAdapter<S, T> implements Observable<S> {
    private final Observable<T> delegate;
    private final S source;

    public ObserversAdapter(final Observable<T> pDelegate, final S pSource) {
        delegate = pDelegate;
        source = pSource;
    }

    @Override
    public S getSource() {
        return source;
    }

    @Override
    public void informCancelObservers() {
        delegate.informCancelObservers();
    }

    @Override
    public void informSuccessObservers(final Checksum pChecksum) {
        delegate.informSuccessObservers(pChecksum);
    }

    @Override
    public void informFailureObservers(final IOException e) {
        delegate.informFailureObservers(e);
    }
}
