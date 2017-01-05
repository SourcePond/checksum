package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.ChecksumException;
import ch.sourcepond.io.checksum.api.MutableChecksum;
import ch.sourcepond.io.checksum.api.UpdateObserver;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * Created by roland on 05.01.17.
 */
abstract class BaseChecksum implements MutableChecksum {
    protected final MessageDigest digest;

    BaseChecksum(final String pAlgorithm) throws NoSuchAlgorithmException {
        assert pAlgorithm != null : "pAlgorithm is null";
        digest = MessageDigest.getInstance(pAlgorithm);
    }


    @Override
    public MutableChecksum addUpdateObserver(final UpdateObserver pObserver) {
        return null;
    }

    @Override
    public MutableChecksum removeUpdateObserver(final UpdateObserver pObserverOrNull) {
        return null;
    }

    @Override
    public Checksum cancel() {
        return null;
    }

    @Override
    public Checksum update() {
        return null;
    }

    @Override
    public Checksum update(final long pIntervalInMilliseconds) {
        return null;
    }

    protected abstract void doUpdate(final long pInterval, final TimeUnit pUnit);

    @Override
    public Checksum update(final long pInterval, final TimeUnit pUnit) {
        return null;
    }

    @Override
    public final String getAlgorithm() {
        return null;
    }

    @Override
    public byte[] getValue() throws ChecksumException {
        return new byte[0];
    }

    @Override
    public String getHexValue() throws ChecksumException {
        return null;
    }

    @Override
    public boolean equalsPrevious() throws ChecksumException {
        return false;
    }

    @Override
    public byte[] getPreviousValue() throws ChecksumException {
        return new byte[0];
    }

    @Override
    public String getPreviousHexValue() throws ChecksumException {
        return null;
    }
}
