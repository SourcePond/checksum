package ch.sourcepond.io.checksum.impl.digest;

import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.ChecksumFactory;
import ch.sourcepond.io.checksum.api.StreamSource;

/**
 * @author rolandhauser
 *
 */
final class DefaultChecksumFactory implements ChecksumFactory {
	private final Executor executor;
	private final DigestFactory digestFactory;

	/**
	 * @param pExecutor
	 */
	public DefaultChecksumFactory(final Executor pExecutor, final DigestFactory pDigestFactory) {
		executor = pExecutor;
		digestFactory = pDigestFactory;
	}

	@Override
	public Checksum create(final String pAlgorithm, final StreamSource pSource) throws NoSuchAlgorithmException {
		return new DefaultChecksum(digestFactory.newDigest(pAlgorithm, pSource), executor);
	}

	@Override
	public Checksum create(final String pAlgorithm, final Path pPath) throws NoSuchAlgorithmException {
		return new DefaultChecksum(digestFactory.newDigest(pAlgorithm, pPath), executor);
	}

	@Override
	public Checksum create(final String pAlgorithm, final URL pUrl) throws NoSuchAlgorithmException {
		return new DefaultChecksum(digestFactory.newDigest(pAlgorithm, new UrlStreamSource(pUrl)), executor);
	}
}
