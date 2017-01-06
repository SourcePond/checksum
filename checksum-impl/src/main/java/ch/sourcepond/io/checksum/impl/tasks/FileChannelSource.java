package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.ChannelSource;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.nio.file.StandardOpenOption.READ;

/**
 * Created by rolandhauser on 06.01.17.
 */
final class FileChannelSource implements ChannelSource {
    private final Path file;

    public FileChannelSource(final Path pFile) {
        file = pFile;
    }

    @Override
    public ReadableByteChannel openChannel() throws IOException {
        return FileChannel.open(file, READ);
    }
}
