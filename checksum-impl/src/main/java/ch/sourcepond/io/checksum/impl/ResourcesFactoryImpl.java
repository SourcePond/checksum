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
import ch.sourcepond.io.checksum.impl.pools.DigesterPoolRegistry;
import ch.sourcepond.io.checksum.impl.resources.InternalResourcesFactory;

import java.net.URL;
import java.nio.file.Path;

/**
 *
 */
public final class ResourcesFactoryImpl implements ResourcesFactory {
    private final InternalResourcesFactory internalResourcesFactory;
    private final DigesterPoolRegistry digesterPoolRegistry;

    // Constructor used by BundleActivator
    public ResourcesFactoryImpl() {
        this(new InternalResourcesFactory(), new DigesterPoolRegistry());
    }

    // Constructor used for testing
    public ResourcesFactoryImpl(final InternalResourcesFactory pInternalResourcesFactory, final DigesterPoolRegistry pDigesterPoolRegistry) {
        internalResourcesFactory = pInternalResourcesFactory;
        digesterPoolRegistry = pDigesterPoolRegistry;
    }

    // Used by Felix DM to determine where to inject
    // service references
    public Object[] getComposition() {
        return new Object[] { internalResourcesFactory, internalResourcesFactory.getTaskFactory() };
    }

    @Override
    public Resource create(final Algorithm pAlgorithm, final ChannelSource pSource) {
        return internalResourcesFactory.newResource(digesterPoolRegistry.get(pAlgorithm), pSource);
    }

    @Override
    public Resource create(final Algorithm pAlgorithm, final StreamSource pSource) {
        return internalResourcesFactory.newResource(digesterPoolRegistry.get(pAlgorithm), pSource);
    }

    @Override
    public Resource create(final Algorithm pAlgorithm, final Path pPath) {
        return internalResourcesFactory.newResource(digesterPoolRegistry.get(pAlgorithm), pPath);
    }

    @Override
    public Resource create(final Algorithm pAlgorithm, final URL pUrl) {
        return internalResourcesFactory.newResource(digesterPoolRegistry.get(pAlgorithm), pUrl);
    }
}
