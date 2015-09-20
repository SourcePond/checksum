package ch.sourcepond.io.checksum.impl.digest;

import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

/**
 * @author rolandhauser
 *
 */
public interface DigestFactory {

	/**
	 * @param pAlgorithm
	 * @param pPath
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	Digest<Path> newPathDigest(String pAlgorithm, Path pPath) throws NoSuchAlgorithmException;

	/**
	 * @param pAlgorithm
	 * @param pUrl
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	Digest<URL> newUrlDigest(String pAlgorithm, URL pUrl) throws NoSuchAlgorithmException;
}
