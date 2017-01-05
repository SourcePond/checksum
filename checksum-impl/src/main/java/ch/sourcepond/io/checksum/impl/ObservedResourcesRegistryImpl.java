package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by rolandhauser on 05.01.17.
 */
public class ObservedResourcesRegistryImpl implements ObservedResourcesRegistry {
    private final ConcurrentMap<Object, BaseResource> resources = new ConcurrentHashMap<>();

    /**
     *
     */
    private static final class Key {
        private final String algorithm;
        private final Object source;

        public Key(final String algorithm, final Object source) {
            this.algorithm = algorithm;
            this.source = source;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Key key = (Key) o;
            return Objects.equals(algorithm, key.algorithm) &&
                    Objects.equals(source, key.source);
        }

        @Override
        public int hashCode() {
            return Objects.hash(algorithm, source);
        }
    }

    private ObservedResource get(Key pKey) {
        BaseResource resource = resources.get(pKey);
        if (resource == null) {
            resource = new BaseResource(this);
            final BaseResource previous = resources.putIfAbsent(pKey, resource);
            if (previous != null) {
                resource = previous;
            }
        }
        return resource.lease();
    }

    private static Key key(Algorithm pAlgorithm, Object pSource) {
        return key(pAlgorithm.toString(), pSource);
    }

    private static Key key(String pAlgorithm, Object pSource) {
        return new Key(pAlgorithm, pSource);
    }

    @Override
    public ObservedResource<ChannelSource> get(final Algorithm pAlgorithm, final ChannelSource pSource) {
        return get(key(pAlgorithm, pSource));
    }

    @Override
    public ObservedResource<StreamSource> get(final Algorithm pAlgorithm, final StreamSource pSource) {
        return get(key(pAlgorithm, pSource));
    }

    @Override
    public ObservedResource<Path> get(final Algorithm pAlgorithm, final Path pPath) throws IOException {
        return get(key(pAlgorithm, pPath));
    }

    @Override
    public ObservedResource<URL> get(final Algorithm pAlgorithm, final URL pUrl) {
        return get(key(pAlgorithm, pUrl));
    }

    @Override
    public ObservedResource<ChannelSource> get(final String pAlgorithm, final ChannelSource pSource) throws NoSuchAlgorithmException {
        return get(key(pAlgorithm, pSource));
    }

    @Override
    public ObservedResource<StreamSource> get(final String pAlgorithm, final StreamSource pSource) throws NoSuchAlgorithmException {
        return get(key(pAlgorithm, pSource));
    }

    @Override
    public ObservedResource<Path> get(final String pAlgorithm, final Path pPath) throws NoSuchAlgorithmException, IOException {
        return get(key(pAlgorithm, pPath));
    }

    @Override
    public ObservedResource<URL> get(final String pAlgorithm, final URL pUrl) throws NoSuchAlgorithmException {
        return get(key(pAlgorithm, pUrl));
    }

    void remove(final BaseResource resourceToBeRemoved) {
        resources.remove(resourceToBeRemoved);
    }
}
