package ch.sourcepond.io.checksum.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;
import org.osgi.framework.BundleContext;

import ch.sourcepond.io.checksum.ChecksumBuilderFactory;
import ch.sourcepond.io.checksum.impl.ChecksumFactoryActivator;

/**
 * @author rolandhauser
 *
 */
public class ChecksumFactoryActivatorTest {
	private final BundleContext context = mock(BundleContext.class);
	private final ChecksumBuilderFactory factory = mock(ChecksumBuilderFactory.class);
	private final ChecksumFactoryActivator activator = new ChecksumFactoryActivator(factory);

	/**
	 * 
	 */
	@Test
	public void verifyDefaultConstructor() {
		// Should not cause an exception
		new ChecksumFactoryActivator();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyStart() throws Exception {
		activator.start(context);
		verify(context).registerService(ChecksumBuilderFactory.class, factory, null);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyStop() throws Exception {
		activator.stop(context);
		verifyZeroInteractions(context);
	}
}
