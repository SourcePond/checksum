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
package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.*;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mockito.Mockito.*;

/**
 *
 */
@SuppressWarnings({"unchecked", "FieldCanBeLocal"})
public class InternalResourcesFactoryTest {
    private final Callable<Checksum> task = mock(Callable.class);
    private final TaskFactory taskFactory = mock(TaskFactory.class);
    private final DigesterPool digesterPool = mock(DigesterPool.class);
    private final ChannelSource channelSource = mock(ChannelSource.class);
    private final StreamSource streamSource = mock(StreamSource.class);
    private final UpdateObserver observer = mock(UpdateObserver.class);
    private Path path;
    private URL url;
    private InternalResourcesFactory factory;

    @Before
    public void setup() throws Exception {
        path = FileSystems.getDefault().getPath("src", "test", "resources", "testfile_01.txt");
        url = getClass().getResource("/testfile_01.txt");
        when(taskFactory.newChannelTask(same(digesterPool), same(observer), notNull(), same(MILLISECONDS),same(0L))).thenReturn(task);
        when(taskFactory.newStreamTask(same(digesterPool), same(observer), notNull(), same(MILLISECONDS), same(0L))).thenReturn(task);
        factory = new InternalResourcesFactory(taskFactory);
        factory.updateExecutor = mock(ExecutorService.class);
    }

    @Test
    public void newChannelResource() {
        final Resource res = factory.newResource(digesterPool, channelSource);
        res.update(observer);
        verify(factory.updateExecutor).submit(task);
    }

    @Test
    public void newStreamResource() {
        final Resource res = factory.newResource(digesterPool, streamSource);
        res.update(observer);
        verify(factory.updateExecutor).submit(task);
    }

    @Test
    public void newPathResource() {
        final Resource res = factory.newResource(digesterPool, path);
        res.update(observer);
        verify(factory.updateExecutor).submit(task);
    }

    @Test
    public void newUrlResource() {
        final Resource res = factory.newResource(digesterPool, url);
        res.update(observer);
        verify(factory.updateExecutor).submit(task);
    }

}
