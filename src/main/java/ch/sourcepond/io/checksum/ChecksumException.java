package ch.sourcepond.io.checksum;

/**
 * @author rolandhauser
 *
 */
public class ChecksumException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1005754965088339032L;

	/**
	 * 
	 */
	public ChecksumException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ChecksumException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ChecksumException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ChecksumException(final Throwable cause) {
		super(cause);
	}

}
