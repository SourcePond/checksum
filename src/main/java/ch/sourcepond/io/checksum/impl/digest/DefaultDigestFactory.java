package ch.sourcepond.io.checksum.impl.digest;

import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author rolandhauser
 *
 */
@Named
@Singleton
public class DefaultDigestFactory implements DigestFactory {

	@Override
	public Digest<Path> newPathDigest(final String pAlgorithm, final Path pPath) throws NoSuchAlgorithmException {
		return new PathDigest(pAlgorithm, pPath);
	}

	@Override
	public Digest<URL> newUrlDigest(final String pAlgorithm, final URL pUrl) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		return null;
	}

}
