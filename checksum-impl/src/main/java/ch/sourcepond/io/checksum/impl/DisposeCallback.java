package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.Resource;

/**
 * Created by rolandhauser on 10.01.17.
 */
@FunctionalInterface
public interface DisposeCallback {

    void dispose(Resource<?> pResource);
}
