package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.api.Resource;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.resources.ResourceFactory;
import ch.sourcepond.io.checksum.impl.store.ResourceStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.net.URL;
import java.nio.file.Path;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by rolandhauser on 12.01.17.
 */
public class ResourcesRegistryImplTest {
    private final ResourceFactory resourceFactory = mock(ResourceFactory.class);
    private final ResourceStore store = mock(ResourceStore.class);
    private final ChannelSource channelSource = mock(ChannelSource.class);
    private final Path path = mock(Path.class);
    private final StreamSource streamSource = mock(StreamSource.class);
    private final Resource<ChannelSource> channelResource = mock(Resource.class);
    private final Resource<Path> pathResource = mock(Resource.class);
    private final Resource<StreamSource> streamResource = mock(Resource.class);
    private final Resource<URL> urlResource = mock(Resource.class);
    private URL url;
    private ResourcesRegistryImpl registry;

    @Before
    public void setup() throws Exception {
        url = new URL("file:///any/url");
        registry = new ResourcesRegistryImpl(resourceFactory, store);
    }

    @Test
    public void getChannelResource() {
        when(store.get(same(SHA256), same(channelSource), notNull())).thenAnswer(new Answer<Resource<ChannelSource>>() {
            @Override
            public Resource<ChannelSource> answer(final InvocationOnMock invocationOnMock) throws Throwable {
                return channelResource;
            }
        });
        assertSame(channelResource, registry.get(SHA256, channelSource));
    }

    @Test
    public void getPathResource() {
        when(store.get(same(SHA256), same(path), notNull())).thenAnswer(new Answer<Resource<Path>>() {
            @Override
            public Resource<Path> answer(final InvocationOnMock invocationOnMock) throws Throwable {
                return pathResource;
            }
        });
        assertSame(pathResource, registry.get(SHA256, path));
    }

    @Test
    public void getStreamResource() {
        when(store.get(same(SHA256), same(streamSource), notNull())).thenAnswer(new Answer<Resource<StreamSource>>() {
            @Override
            public Resource<StreamSource> answer(final InvocationOnMock invocationOnMock) throws Throwable {
                return streamResource;
            }
        });
        assertSame(streamResource, registry.get(SHA256, streamSource));
    }

    @Test
    public void getURLResource() {
        when(store.get(same(SHA256), same(url), notNull())).thenAnswer(new Answer<Resource<URL>>() {
            @Override
            public Resource<URL> answer(final InvocationOnMock invocationOnMock) throws Throwable {
                return urlResource;
            }
        });
        assertSame(urlResource, registry.get(SHA256, url));
    }
}
