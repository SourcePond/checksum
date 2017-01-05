package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.Algorithm;
import ch.sourcepond.io.checksum.api.ChecksumFactory;
import ch.sourcepond.io.checksum.api.MutableChecksum;
import ch.sourcepond.io.checksum.api.StreamSource;

import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

/**
 * Created by roland on 04.01.17.
 */
public class DefaultChecksumFactory implements ChecksumFactory {

    @Override
    public MutableChecksum create(final Algorithm pAlgorithm, final StreamSource pSource) {
        return null;
    }

    @Override
    public MutableChecksum create(final Algorithm pAlgorithm, final Path pPath) {
        return null;
    }

    @Override
    public MutableChecksum create(final Algorithm pAlgorithm, final URL pUrl) {
        return null;
    }

    @Override
    public MutableChecksum create(final String pAlgorithm, final StreamSource pSource) throws NoSuchAlgorithmException {
        return null;
    }

    @Override
    public MutableChecksum create(final String pAlgorithm, final Path pPath) throws NoSuchAlgorithmException {
        return null;
    }

    @Override
    public MutableChecksum create(final String pAlgorithm, final URL pUrl) throws NoSuchAlgorithmException {
        return null;
    }
}
