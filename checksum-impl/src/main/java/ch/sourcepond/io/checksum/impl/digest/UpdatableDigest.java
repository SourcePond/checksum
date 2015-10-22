package ch.sourcepond.io.checksum.impl.digest;

import static java.security.MessageDigest.getInstance;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author rolandhauser
 *
 * @param <T>
 */
public abstract class UpdatableDigest<T> extends Digest<T> implements Cancellable {
	// To safe as much system resources as possible, we do not hold hard
	// references to the digester.
	private WeakReference<MessageDigest> digestRef;

	/**
	 * @param pAlgorithm
	 * @throws NoSuchAlgorithmException
	 */
	UpdatableDigest(final String pAlgorithm, final T pSource) throws NoSuchAlgorithmException {
		super(pAlgorithm, pSource);
		digestRef = new WeakReference<MessageDigest>(getInstance(pAlgorithm));
	}

	/**
	 * @return
	 */
	protected final MessageDigest getDigest() {
		// Initialize the temporary hard reference to the digester; this must be
		// set to null after the update has been performed.
		MessageDigest tempDigest = digestRef.get();
		if (tempDigest == null) {
			try {
				tempDigest = getInstance(getAlgorithm());
			} catch (final NoSuchAlgorithmException e) {
				// This can never happen because it has already been validated
				// during construction that the algorithm is available.
			}
			digestRef = new WeakReference<MessageDigest>(tempDigest);
		}
		return tempDigest;
	}

	/**
	 * @param pChannel
	 * @throws IOException
	 */
	public abstract byte[] updateDigest() throws IOException;
}
