package ch.sourcepond.io.checksum.impl.digest;

/**
 * @author rolandhauser
 *
 */
public abstract class Digest {
	private final String algorithm;

	/**
	 * @param pAlgorithm
	 */
	Digest(final String pAlgorithm) {
		algorithm = pAlgorithm;
	}

	/**
	 * 
	 */
	public abstract void cancel();

	/**
	 * @return
	 */
	public String getAlgorithm() {
		return algorithm;
	}
}
