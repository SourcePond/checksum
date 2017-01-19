package ch.sourcepond.io.checksum.impl.resources;

import org.junit.Before;
import org.junit.Test;

import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystems;

import static org.junit.Assert.*;

/**
 *
 */
public class FileChannelSourceTest {
    private FileChannelSource source;

    @Before
    public void setup() throws Exception {
        source = new FileChannelSource(FileSystems.getDefault().getPath("src", "test", "resources", "testfile_01.txt"));
    }

    @Test
    public void openChannel() throws Exception {
        try (final ReadableByteChannel ch = source.openChannel()) {
           assertTrue(ch.isOpen());
        }
    }
}
