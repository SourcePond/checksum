package ch.sourcepond.io.checksum.impl.digest;

import java.io.IOException;

/**
 * @author rolandhauser
 *
 * @param <T>
 */
public abstract class UpdatableDigest<T> extends Digest {
	private final T source;

	/**
	 * @param pAlgorithm
	 */
	UpdatableDigest(final String pAlgorithm, final T pSource) {
		super(pAlgorithm);
		source = pSource;
	}

	/**
	 * @return
	 */
	public T getSource() {
		return source;
	}

	/**
	 * @param pChannel
	 * @throws IOException
	 */
	public abstract byte[] updateDigest() throws IOException;
}
