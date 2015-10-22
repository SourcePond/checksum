package ch.sourcepond.io.checksum.impl.digest;

import java.io.IOException;

/**
 * @author rolandhauser
 *
 * @param <T>
 */
public abstract class UpdatableDigest<T> extends Digest<T> {

	/**
	 * @param pAlgorithm
	 */
	UpdatableDigest(final String pAlgorithm, final T pSource) {
		super(pAlgorithm, pSource);
	}

	/**
	 * @param pChannel
	 * @throws IOException
	 */
	public abstract byte[] updateDigest() throws IOException;
}
