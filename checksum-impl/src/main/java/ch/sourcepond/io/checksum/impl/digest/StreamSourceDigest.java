package ch.sourcepond.io.checksum.impl.digest;

import static ch.sourcepond.io.checksum.impl.digest.DigestHelper.perform;

import java.io.IOException;

import ch.sourcepond.io.checksum.api.StreamSource;

/**
 * @author rolandhauser
 *
 */
final class StreamSourceDigest extends UpdatableDigest<StreamSource> {

	StreamSourceDigest(final String pAlgorithm, final StreamSource pSource) {
		super(pAlgorithm, pSource);
	}

	@Override
	protected byte[] doUpdateDigest() throws IOException {
		return perform(getDigest(), this, getSource().openStream());
	}

}
