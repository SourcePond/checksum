package ch.sourcepond.io.checksum.impl.digest;

import java.io.IOException;

/**
 * @author rolandhauser
 *
 * @param <T>
 */
public interface UpdatableDigest<T> extends Digest {

	/**
	 * @return
	 */
	T getSource();

	/**
	 * @param pChannel
	 * @throws IOException
	 */
	byte[] updateDigest() throws IOException;
}
