package ch.sourcepond.io.checksum;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.sourcepond.testing.StubServiceFactory;

/**
 * @author rolandhauser
 *
 */
public class ExecutorServiceFactory implements StubServiceFactory<ExecutorService> {

	@Override
	public ExecutorService create() {
		return Executors.newFixedThreadPool(1);
	}

	@Override
	public void destroy(final ExecutorService pService) {
		pService.shutdown();
	}

}
