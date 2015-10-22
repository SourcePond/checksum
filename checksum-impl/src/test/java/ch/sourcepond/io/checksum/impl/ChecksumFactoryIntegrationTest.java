package ch.sourcepond.io.checksum.impl;

import java.nio.file.Path;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * @author rolandhauser
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public abstract class ChecksumFactoryIntegrationTest extends ChecksumFactoryTest {

	/**
	 * @param pFs
	 * @return
	 */
	protected Path getResourceRootPath() {
		// Change directory in sub-modules
		return super.getResourceRootPath().getParent().resolve("checksum-impl");
	}
}
