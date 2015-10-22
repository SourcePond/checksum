package ch.sourcepond.io.checksum.impl.digest;

/**
 * @author rolandhauser
 *
 */
public abstract class Digest<T> implements Cancellable {
	private final String algorithm;
	private final T source;
	private volatile boolean cancelled;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.digest.Cancellable#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * @param pCancelled
	 */
	protected void setCancelled(final boolean pCancelled) {
		cancelled = pCancelled;
	}

	/**
	 * 
	 */
	public void cancel() {
		setCancelled(true);
	}

	/**
	 * @return
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	@Override
	protected final void finalize() throws Throwable {
		cancel();
	}
}
