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

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.api.Resource;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.tasks.ResultFuture;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;
import ch.sourcepond.io.checksum.impl.tasks.UpdateTask;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.notNull;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
@SuppressWarnings({"unchecked", "FieldCanBeLocal"})
public class InternalResourcesFactoryTest {
    private final ScheduledExecutorService updateExecutor = mock(ScheduledExecutorService.class);
    private final UpdateTask initialTask = mock(UpdateTask.class);
    private final UpdateTask task = mock(UpdateTask.class);
    private final ResultFuture result = mock(ResultFuture.class);
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
        when(task.getFuture()).thenReturn(result);
        when(taskFactory.newChannelTask(same(digesterPool), notNull(), same(MILLISECONDS), same(0L))).thenReturn(task);
        when(taskFactory.newStreamTask(same(digesterPool), notNull(), same(MILLISECONDS), same(0L))).thenReturn(task);
        factory = new InternalResourcesFactory(taskFactory);
        factory.setUpdateExecutor(updateExecutor);
    }

    @Test
    public void newChannelResource() throws IOException {
        final Resource res = factory.newResource(digesterPool, channelSource);
        res.update(observer);
        verify(updateExecutor).execute(task);
    }

    @Test
    public void newStreamResource() throws IOException {
        final Resource res = factory.newResource(digesterPool, streamSource);
        res.update(observer);
        verify(updateExecutor).execute(task);
    }

    @Test
    public void newPathResource() throws IOException {
        final Resource res = factory.newResource(digesterPool, path);
        res.update(observer);
        verify(updateExecutor).execute(task);
    }

    @Test
    public void newUrlResource() throws IOException {
        final Resource res = factory.newResource(digesterPool, url);
        res.update(observer);
        verify(updateExecutor).execute(task);
    }

}
