package ch.sourcepond.io.checksum.impl.digest;

import java.util.concurrent.Callable;

/**
 * @author rolandhauser
 *
 */
public interface ImmutableDigest extends Digest, Callable<byte[]> {

}
