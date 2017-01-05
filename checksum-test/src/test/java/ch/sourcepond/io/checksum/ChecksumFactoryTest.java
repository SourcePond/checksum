/*Copyright (C) 2015 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.io.checksum;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static ch.sourcepond.io.checksum.api.ObservedResourcesRegistry.LISTENER_EXECUTOR_ATTRIBUTE;
import static ch.sourcepond.io.checksum.api.ObservedResourcesRegistry.UPDATE_EXECUTOR_ATTRIBUTE;
import static ch.sourcepond.testing.OptionsHelper.blueprintBundles;
import static ch.sourcepond.testing.OptionsHelper.defaultOptions;
import static ch.sourcepond.testing.OptionsHelper.stubService;
import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.ObservedResourcesRegistry;
import ch.sourcepond.io.checksum.api.StreamSource;

/**
 * @author rolandhauser
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ChecksumFactoryTest {
	private static final String FIRST_EXPECTED_HASH = "40ab41c711d6979c8bfb9dae2022d79e4fa43b79bf5c74cc8d291936586a4778";
	private static final String SECOND_EXPECTED_HASH = "da821a59243b5f99e0d14c1e93b9e00b8e9632b60a07cc8168b78128773dfa31";
	private static final String TEST_CONTENT_FILE_NAME = "content.txt";
	private static final String FIRST_CONTENT_FILE_NAME = "first_content.txt";
	private static final String SECOND_CONTENT_FILE_NAME = "second_content.txt";
	private static final Path TEST_FILE = getDefault().getPath(SystemUtils.USER_DIR, "target", "test-classes",
			TEST_CONTENT_FILE_NAME);

	/**
	 * 
	 */
	@Inject
	private ObservedResourcesRegistry factory;

	/**
	 * @return
	 * @throws Exception
	 */
	@Configuration
	public Option[] config() throws Exception {
		return options(defaultOptions(getClass().getPackage().getName()), blueprintBundles(),
				stubService(ExecutorService.class).withFactory(ExecutorServiceFactory.class)
						.addProperty(UPDATE_EXECUTOR_ATTRIBUTE, "true").addProperty(LISTENER_EXECUTOR_ATTRIBUTE, "true")
						.build(),
				mavenBundle("commons-codec", "commons-codec").versionAsInProject());
	}

	/**
	 * @param pTestContentFileName
	 * @return
	 * @throws IOException
	 */
	private InputStream asStream(final String pTestContentFileName) throws IOException {
		return newInputStream(resolveResourcesDirectory().resolve(pTestContentFileName));
	}

	/**
	 * 
	 */
	private void copyContent(final String pTestContentFileName) throws IOException {
		try (final InputStream in = asStream(pTestContentFileName)) {
			copy(in, TEST_FILE, REPLACE_EXISTING);
		}
	}

	/**
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void verifyCreateChecksumFromStream() throws Exception {
		copyContent(FIRST_CONTENT_FILE_NAME);
		final Checksum chsm = factory.create(SHA256, new StreamSource() {

			@Override
			public InputStream openStream() throws IOException {
				return asStream(FIRST_CONTENT_FILE_NAME);
			}
		}).update(3, SECONDS);
		assertEquals(FIRST_EXPECTED_HASH, chsm.getHexValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void verifyCreateFileChecksum() throws Exception {
		copyContent(FIRST_CONTENT_FILE_NAME);
		final Checksum chsm = factory.create(SHA256, TEST_FILE).update(3, SECONDS);
		assertEquals(EMPTY, chsm.getPreviousHexValue());
		assertEquals(FIRST_EXPECTED_HASH, chsm.getHexValue());

		copyContent(SECOND_CONTENT_FILE_NAME);
		chsm.update();
		assertEquals(FIRST_EXPECTED_HASH, chsm.getPreviousHexValue());
		assertEquals(SECOND_EXPECTED_HASH, chsm.getHexValue());
	}

	/**
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void verifyCreateUrlChecksum() throws Exception {
		copyContent(FIRST_CONTENT_FILE_NAME);
		final Checksum chsm = factory.create(SHA256, TEST_FILE.toUri().toURL()).update(3, SECONDS);
		assertEquals(EMPTY, chsm.getPreviousHexValue());
		assertEquals(FIRST_EXPECTED_HASH, chsm.getHexValue());

		copyContent(SECOND_CONTENT_FILE_NAME);
		chsm.update();
		assertEquals(FIRST_EXPECTED_HASH, chsm.getPreviousHexValue());
		assertEquals(SECOND_EXPECTED_HASH, chsm.getHexValue());
	}

	/**
	 * @return
	 */
	private Path resolveResourcesDirectory() {
		return getDefault().getPath(USER_DIR).resolve("src").resolve("test").resolve("resources");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyDirectoryChecksum() throws Exception {
		final Checksum chsm = factory.create(SHA256, resolveResourcesDirectory()).update(5, SECONDS);
		assertEquals("dd3e119c99983d19b13fd51020f0f2562cde3788e5d36b7666b961bb159f16c8", chsm.getHexValue());
	}
}
