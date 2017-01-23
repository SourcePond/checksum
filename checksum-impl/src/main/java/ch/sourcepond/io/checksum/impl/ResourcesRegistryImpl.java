/*Copyright (C) 2017 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.*;
import ch.sourcepond.io.checksum.impl.resources.ResourceFactory;
import ch.sourcepond.io.checksum.impl.store.ResourceStore;

import java.net.URL;
import java.nio.file.Path;

/**
 *
 */
public final class ResourcesRegistryImpl implements ResourcesRegistry {
    private final ResourceFactory resourceFactory;
    private final ResourceStore store;

    public ResourcesRegistryImpl(final ResourceFactory pResourceFactory,
                          final ResourceStore pStore) {
        resourceFactory = pResourceFactory;
        store = pStore;
    }

    @Override
    public Resource<ChannelSource> get(final Algorithm pAlgorithm, final ChannelSource pSource) {
        return store.get(pAlgorithm, pSource, pool -> resourceFactory.newResource(pool, pSource));
    }

    @Override
    public Resource<StreamSource> get(final Algorithm pAlgorithm, final StreamSource pSource) {
        return store.get(pAlgorithm, pSource, pool -> resourceFactory.newResource(pool, pSource));
    }

    @Override
    public Resource<Path> get(final Algorithm pAlgorithm, final Path pPath) {
        return store.get(pAlgorithm, pPath, pool -> resourceFactory.newResource(pool, pPath));
    }

    @Override
    public Resource<URL> get(final Algorithm pAlgorithm, final URL pUrl) {
        return store.get(pAlgorithm, pUrl, pool -> resourceFactory.newResource(pool, pUrl));
    }
}
