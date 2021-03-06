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

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.api.Resource;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.pools.DigesterPoolRegistry;
import ch.sourcepond.io.checksum.impl.resources.InternalResourcesFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ResourcesFactoryImplTest {
    private final InternalResourcesFactory internalResourcesFactory = mock(InternalResourcesFactory.class);
    private final DigesterPoolRegistry digesterPoolRegistry = mock(DigesterPoolRegistry.class);
    private final DigesterPool digesterPool = mock(DigesterPool.class);
    private final ChannelSource channelSource = mock(ChannelSource.class);
    private final Path path = mock(Path.class);
    private final StreamSource streamSource = mock(StreamSource.class);
    private final Resource resource = mock(Resource.class);
    private URL url;
    private ResourceProducerImpl factory;

    @Before
    public void setup() throws Exception {
        url = new URL("file:///any/url");
        factory = new ResourceProducerImpl(internalResourcesFactory, digesterPoolRegistry);
        when(digesterPoolRegistry.get(SHA256)).thenReturn(digesterPool);
    }

    @Test
    public void verifyClose() {
        factory.close();
        verify(internalResourcesFactory).close();
    }

    @Test
    public void newChannelResource() throws IOException {
        when(internalResourcesFactory.newResource(digesterPool, channelSource)).thenReturn(resource);
        assertSame(resource, factory.create(SHA256, channelSource));
    }

    @Test
    public void newPathResource() throws IOException {
        when(internalResourcesFactory.newResource(digesterPool, path)).thenReturn(resource);
        assertSame(resource, factory.create(SHA256, path));
    }

    @Test
    public void newStreamResource() throws IOException {
        when(internalResourcesFactory.newResource(digesterPool, streamSource)).thenReturn(resource);
        assertSame(resource, factory.create(SHA256, streamSource));
    }

    @Test
    public void newURLResource() throws IOException {
        when(internalResourcesFactory.newResource(digesterPool, url)).thenReturn(resource);
        assertSame(resource, factory.create(SHA256, url));
    }
}
