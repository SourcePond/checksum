package ch.sourcepond.io.checksum.impl.digest;

/**
 * @author rolandhauser
 *
 */
public abstract class Digest<T> {
	private final String algorithm;
	private final T source;

	/**
	 * @param pAlgorithm
	 */
	Digest(final String pAlgorithm, T pSource) {
		algorithm = pAlgorithm;
		source = pSource;
	}

	/**
	 * @return
	 */
	public T getSource() {
		return source;
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
