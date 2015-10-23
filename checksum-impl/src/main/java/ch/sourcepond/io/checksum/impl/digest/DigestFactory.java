package ch.sourcepond.io.checksum.impl.digest;

import java.io.InputStream;
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
	 * @param pSource
	 * @return
	 */
	InputStreamDigest newDigestTask(String pAlgorithm, InputStream pSource);

	/**
	 * @param pAlgorithm
	 * @param pPath
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	UpdatableDigest<Path> newDigest(String pAlgorithm, Path pPath);

	/**
	 * @param pAlgorithm
	 * @param pUrl
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	UpdatableDigest<URL> newDigest(String pAlgorithm, URL pUrl);
}
