package ch.sourcepond.io.checksum.integrationtest.osgi;

import static ch.sourcepond.testing.bundle.OptionsHelper.defaultOptions;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import ch.sourcepond.io.checksum.ChecksumFactory;
import ch.sourcepond.io.checksum.impl.ChecksumFactoryTest;

/**
*
*/
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OSGiChecksumFactoryITCase extends ChecksumFactoryTest {

	/**
	 * 
	 */
	@Inject
	private ChecksumFactory factory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.ChecksumFactoryTest#getFactory()
	 */
	@Override
	protected ChecksumFactory getFactory() {
		return factory;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@Configuration
	public Option[] config() throws Exception {
		return options(mavenBundle("ch.sourcepond.utils", "checksum-api").versionAsInProject(),
				mavenBundle("commons-codec", "commons-codec").versionAsInProject(), defaultOptions());
	}
}
