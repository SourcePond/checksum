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

import ch.sourcepond.commons.smartswitch.api.SmartSwitchFactory;
import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.api.Resource;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.tasks.TaskFactory;

import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Factory to create {@link Resource} instances for different sources.
 */
public class InternalResourcesFactory {
    private final ExecutorService updateExecutor;
    private final TaskFactory taskFactory;

    public InternalResourcesFactory(final SmartSwitchFactory pSmartSwitch, final TaskFactory pTaskFactory) {
        updateExecutor = pSmartSwitch.whenService(ExecutorService.class).
                withFilter("(sourcepond.io.checksum.updateexecutor=*)").
                isUnavailableThenUse(() -> newFixedThreadPool(3)).
                insteadAndExecuteWhenAvailable(ExecutorService::shutdown);
        taskFactory = pTaskFactory;
    }

    public Resource newResource(final DigesterPool pDigesterPool, final ChannelSource pSource) {
        return new ChannelResource(
                updateExecutor,
                pSource,
                pDigesterPool,
                taskFactory);
    }

    public Resource newResource(final DigesterPool pDigesterPool, final Path pSource) {
        return new ChannelResource(
                updateExecutor,
                new FileChannelSource(pSource),
                pDigesterPool,
                taskFactory);
    }

    public Resource newResource(final DigesterPool pDigesterPool, final StreamSource pSource) {
        return new StreamResource(
                updateExecutor,
                pSource,
                pDigesterPool,
                taskFactory);
    }

    public Resource newResource(final DigesterPool pDigesterPool, final URL pSource) {
        return new StreamResource(
                updateExecutor,
                new URLStreamSource(pSource),
                pDigesterPool,
                taskFactory);
    }
}
