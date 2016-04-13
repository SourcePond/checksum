package ch.sourcepond.io.checksum.impl;

import java.io.IOException;

/**
 * @author rolandhauser
 *
 */
interface UpdateStrategy extends Cancellable {

	byte[] digest();

	void cancel();

	String getAlgorithm();

	void update() throws IOException;
}
