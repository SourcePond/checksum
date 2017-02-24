package ch.sourcepond.io.checksum;

import ch.sourcepond.io.checksum.api.CalculationObserver;
import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.Resource;
import ch.sourcepond.io.checksum.api.ResourcesFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static ch.sourcepond.testing.OptionsHelper.karafContainer;
import static java.nio.file.Files.newBufferedWriter;
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

/**
 *
 */
@RunWith(PaxExam.class)
public class ResourcesTest {
    private static final String FIRST_EXPECTED_SHA_256_HASH = "b0a0a864cf2eb7c20a25bfe12f4cddc6070809e5da8f5da226234a258d17d336";
    private static final String SECOND_EXPECTED_SHA_256_HASH = "6c0f8adc6aac283543b974b395a8f9bb61e837076b2118fb9fbec71e1540b28e";
    private final File testfile = new File("target", "testfile_01.txt");

    @SuppressWarnings("CanBeFinal")
    @Inject
    private ResourcesFactory registry;

    @Configuration
    public Option[] config() {
        return new Option[]{
                karafContainer(features(maven()
                        .groupId("ch.sourcepond.io")
                        .artifactId("checksum-feature")
                        .classifier("features")
                        .type("xml")
                        .versionAsInProject(), "checksum-feature"))
        };
    }

    @Before
    public void setup() throws IOException {
        try (final InputStream in = getClass().getResourceAsStream("/testfile_01.txt")) {
            try (final FileOutputStream out = new FileOutputStream(testfile)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
        }
    }

    private void appendToTestFile() throws IOException {
        try (final BufferedWriter writer = newBufferedWriter(testfile.toPath(), StandardOpenOption.APPEND)) {
            writer.write(FIRST_EXPECTED_SHA_256_HASH);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void tearDown() throws IOException {
        testfile.delete();
    }

    @Test
    public void verifyPathResource() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        final List<Checksum> observerChecksums = Collections.synchronizedList(new ArrayList<>());
        final Resource resource = registry.create(SHA256, testfile.toPath());

        CalculationObserver observer = (pPrevious, pCurrent) -> {
            observerChecksums.add(pPrevious);
            observerChecksums.add(pCurrent);
            latch.countDown();
        };

        final Checksum firstChecksum = resource.update(observer).get();
        appendToTestFile();
        final Checksum secondChecksum = resource.update(observer).get();
        assertEquals(FIRST_EXPECTED_SHA_256_HASH, firstChecksum.getHexValue());
        assertEquals(SECOND_EXPECTED_SHA_256_HASH, secondChecksum.getHexValue());

        latch.await();
        assertEquals(4, observerChecksums.size());
        observerChecksums.sort(Comparator.comparing(Checksum::getTimestamp));

        assertEquals("", observerChecksums.get(0).getHexValue());
        assertEquals(FIRST_EXPECTED_SHA_256_HASH, observerChecksums.get(1).getHexValue());
        assertEquals(FIRST_EXPECTED_SHA_256_HASH, observerChecksums.get(2).getHexValue());
        assertEquals(SECOND_EXPECTED_SHA_256_HASH, observerChecksums.get(3).getHexValue());
    }
}
