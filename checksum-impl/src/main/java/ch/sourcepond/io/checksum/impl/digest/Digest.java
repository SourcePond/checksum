package ch.sourcepond.io.checksum.impl.digest;

/**
 * @author rolandhauser
 *
 */
public interface Digest {

	/**
	 * 
	 */
	void cancel();

	/**
	 * @return
	 */
	String getAlgorithm();
}
