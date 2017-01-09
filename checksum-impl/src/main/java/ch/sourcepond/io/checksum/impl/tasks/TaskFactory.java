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
package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.ResourceContext;

import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Created by rolandhauser on 06.01.17.
 */
public class TaskFactory {

    Runnable newFileTask(final ResourceContext pResource, final TimeUnit pUnit, final long pInterval, final Path pFile) {
        return newChannelTask(pResource, pUnit, pInterval, new FileChannelSource(pFile));
    }

    Runnable newChannelTask(final ResourceContext pResource, final TimeUnit pUnit, final long pInterval, final ChannelSource pChannelSource) {
        return new ChannelUpdateTask(pResource, pChannelSource, new DataReader(pUnit, pInterval));
    }

    Runnable newStreamTask(final ResourceContext pResource, final TimeUnit pUnit, final long pInterval, final StreamSource pStreamSource) {
        return new StreamUpdateTask(pResource, pStreamSource, new DataReader(pUnit, pInterval));
    }

    Runnable newURLTask(final ResourceContext pResource, final TimeUnit pUnit, final long pInterval, final URL pUrl) {
        return newStreamTask(pResource, pUnit, pInterval, new URLStreamSource(pUrl));
    }
}
