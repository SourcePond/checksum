package ch.sourcepond.io.checksum;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.Resource;
import ch.sourcepond.io.checksum.api.ResourcesRegistry;
import ch.sourcepond.io.checksum.api.SuccessObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static ch.sourcepond.testing.OptionsHelper.blueprintBundles;
import static java.nio.file.Files.newBufferedWriter;
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Created by rolandhauser on 17.01.17.
 */
@RunWith(PaxExam.class)
public class ResourcesTests {
    private static final String FIRST_EXPECTED_SHA_256_HASH = "b0a0a864cf2eb7c20a25bfe12f4cddc6070809e5da8f5da226234a258d17d336";
    private static final String SECOND_EXPECTED_SHA_256_HASH = "6c0f8adc6aac283543b974b395a8f9bb61e837076b2118fb9fbec71e1540b28e";
    private final File testfile = new File("target", "testfile_01.txt");

    @Inject
    private ResourcesRegistry registry;

    @Configuration
    public Option[] configure() {
        return options(
                blueprintBundles(),
                mavenBundle("ch.sourcepond.commons", "smartswitch-api").versionAsInProject(),
                mavenBundle("ch.sourcepond.commons", "smartswitch-impl").versionAsInProject(),
                mavenBundle("ch.sourcepond.io", "checksum-api").versionAsInProject(),
                mavenBundle("ch.sourcepond.io", "checksum-impl").versionAsInProject(),
                junitBundles());
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

    @After
    public void tearDown() throws IOException {
        testfile.delete();
    }

    @Test
    public void verifyPathResource() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        final List<Checksum> observerChecksums = new CopyOnWriteArrayList<>();
        final Resource<Path> resource = registry.get(SHA256, testfile.toPath());
        resource.addSuccessObserver(new SuccessObserver<Path>() {
            @Override
            public void updateSucceeded(final Path pSource, final Checksum pPrevious, final Checksum pCurrent) {
                observerChecksums.add(pPrevious);
                observerChecksums.add(pCurrent);
                latch.countDown();
            }
        });
        final Checksum firstChecksum = resource.update().get();
        appendToTestFile();
        final Checksum secondChecksum = resource.update().get();
        assertEquals(FIRST_EXPECTED_SHA_256_HASH, firstChecksum.getHexValue());
        assertEquals(SECOND_EXPECTED_SHA_256_HASH, secondChecksum.getHexValue());

        latch.await();
        assertEquals(4, observerChecksums.size());
        observerChecksums.sort((o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()));

        assertEquals("", observerChecksums.get(0).getHexValue());
        assertEquals(FIRST_EXPECTED_SHA_256_HASH, observerChecksums.get(1).getHexValue());
        assertEquals(FIRST_EXPECTED_SHA_256_HASH, observerChecksums.get(2).getHexValue());
        assertEquals(SECOND_EXPECTED_SHA_256_HASH, observerChecksums.get(3).getHexValue());
    }
}
