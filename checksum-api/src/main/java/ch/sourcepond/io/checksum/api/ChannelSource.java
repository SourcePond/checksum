package ch.sourcepond.io.checksum.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by rolandhauser on 05.01.17.
 */
public interface ChannelSource {

    /**
     * Opens an readable channel to the underlying source of this object. Multiple
     * calls to this method will open individual {@link ReadableByteChannel} instances.
     *
     * @return {@link ReadableByteChannel} instance, never {@code null}
     * @throws IOException
     *             Thrown, if the readable channel could not be opened for some
     *             reason.
     */
    ReadableByteChannel openChannel() throws IOException;
}
