package ch.sourcepond.io.checksum;

import static ch.sourcepond.testing.bundle.OptionsHelper.defaultOptions;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;

import ch.sourcepond.io.checksum.impl.ChecksumFactoryIntegrationTest;

/**
 * @author rolandhauser
 *
 */
public class ChecksumFactoryTest extends ChecksumFactoryIntegrationTest {

	/**
	 * 
	 */
	@Inject
	private ChecksumBuilderFactory factory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.ChecksumFactoryTest#getFactory()
	 */
	@Override
	protected ChecksumBuilderFactory getBuilderFactory() {
		return factory;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@Configuration
	public Option[] config() throws Exception {
		return options(defaultOptions("ch.sourcepond.io.checksum"),
				mavenBundle("commons-codec", "commons-codec").versionAsInProject());
	}
}
