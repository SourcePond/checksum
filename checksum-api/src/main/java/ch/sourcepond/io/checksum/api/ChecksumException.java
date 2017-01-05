package ch.sourcepond.io.checksum.api;

/**
 * Created by roland on 04.01.17.
 */
public class ChecksumException extends Exception {
    private final Checksum checksum;

    public ChecksumException(String message, Throwable cause, Checksum checksum) {
        super(message, cause);
        this.checksum = checksum;
    }
}
