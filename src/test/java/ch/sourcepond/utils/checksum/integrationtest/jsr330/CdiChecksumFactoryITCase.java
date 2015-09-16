package ch.sourcepond.utils.checksum.integrationtest.jsr330;

import static com.google.inject.Guice.createInjector;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;

import ch.sourcepond.utils.checksum.ChecksumFactory;
import ch.sourcepond.utils.checksum.impl.ChecksumFactoryTest;
import ch.sourcepond.utils.checksum.impl.DefaultChecksumFactory;

/**
 * @author rolandhauser
 *
 */
public class CdiChecksumFactoryITCase extends ChecksumFactoryTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.utils.checksum.impl.ChecksumFactoryTest#getFactory()
	 */
	@Override
	protected ChecksumFactory getFactory() {
		final Injector injector = createInjector(new Module() {

			@Override
			public void configure(final Binder binder) {
				binder.bind(ChecksumFactory.class).to(DefaultChecksumFactory.class);
			}
		});
		return injector.getInstance(ChecksumFactory.class);
	}

}
