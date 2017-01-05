package ch.sourcepond.io.checksum.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * Created by roland on 05.01.17.
 */
public class NIOChecksum extends BaseChecksum {
    private final Path path;

    public NIOChecksum(final String pAlgorithm, final Path pPath) throws NoSuchAlgorithmException {
        super(pAlgorithm);
        path = pPath;
    }

    @Override
    protected void doUpdate(final long pInterval, final TimeUnit pUnit) {
    }
}
