package ch.sourcepond.io.checksum.impl.digest;

import java.io.IOException;

/**
 * @author rolandhauser
 *
 * @param <T>
 */
public interface Digest<T> {

	/**
	 * @return
	 */
	T getSource();

	/**
	 * @return
	 */
	String getAlgorithm();

	/**
	 * @param pChannel
	 * @throws IOException
	 */
	byte[] updateDigest() throws IOException;

	/**
	 * 
	 */
	void cancel();
}
