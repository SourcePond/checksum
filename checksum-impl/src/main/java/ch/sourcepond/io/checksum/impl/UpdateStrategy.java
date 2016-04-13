package ch.sourcepond.io.checksum.impl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author rolandhauser
 *
 */
interface UpdateStrategy extends Cancellable {

	byte[] digest();

	void cancel();

	String getAlgorithm();

	void update(long pInterval, TimeUnit pUnit) throws IOException, InterruptedException;
}
