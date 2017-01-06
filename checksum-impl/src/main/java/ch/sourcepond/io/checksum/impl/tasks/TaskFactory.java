package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.ChannelSource;
import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.ResourceCallback;

import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Created by rolandhauser on 06.01.17.
 */
public class TaskFactory {

    Runnable newFileTask(final ResourceCallback pResource, final TimeUnit pUnit, final long pInterval, final Path pFile) {
        return newChannelTask(pResource, pUnit, pInterval, new FileChannelSource(pFile));
    }

    Runnable newChannelTask(final ResourceCallback pResource, final TimeUnit pUnit, final long pInterval, final ChannelSource pChannelSource) {
        return new ChannelUpdateTask(pResource, pInterval, pUnit, pChannelSource);
    }

    Runnable newStreamTask(final ResourceCallback pResource, final TimeUnit pUnit, final long pInterval, final StreamSource pStreamSource) {
        return new StreamUpdateTask(pResource, pInterval, pUnit, pStreamSource);
    }

    Runnable newURLTask(final ResourceCallback pResource, final TimeUnit pUnit, final long pInterval, final URL pUrl) {
        return newStreamTask(pResource, pUnit, pInterval, new URLStreamSource(pUrl));
    }
}
