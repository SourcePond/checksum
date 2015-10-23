package ch.sourcepond.io.checksum.impl.digest;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author rolandhauser
 *
 */
@Named // Necessary to make this component work with Eclipse Sisu
@Singleton
public class DefaultDigestFactory implements DigestFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.io.checksum.impl.digest.DigestFactory#newDigestTask(java.
	 * lang.String, java.io.InputStream)
	 */
	@Override
	public InputStreamDigest newDigestTask(final String pAlgorithm, final InputStream pSource) {
		return new InputStreamDigest(pAlgorithm, pSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.io.checksum.impl.digest.DigestFactory#newPathDigest(java.
	 * lang.String, java.nio.file.Path)
	 */
	@Override
	public UpdatableDigest<Path> newDigest(final String pAlgorithm, final Path pPath) {
		return new PathDigest(pAlgorithm, pPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.io.checksum.impl.digest.DigestFactory#newUrlDigest(java.
	 * lang.String, java.net.URL)
	 */
	@Override
	public UpdatableDigest<URL> newDigest(final String pAlgorithm, final URL pUrl) {
		return new UrlDigest(pAlgorithm, pUrl);
	}
}
