/*Copyright (C) 2017 Roland Hauser, <sourcepond@gmail.com>

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

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.Resource;
import ch.sourcepond.io.checksum.api.ResourceProducer;
import ch.sourcepond.io.checksum.api.ResourceProducerFactory;
import ch.sourcepond.io.checksum.api.Update;
import ch.sourcepond.io.checksum.api.UpdateObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static ch.sourcepond.io.checksum.api.Algorithm.SHA256;
import static ch.sourcepond.testing.OptionsHelper.karafContainer;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

/**
 *
 */
public abstract class ResourcesTest {

    private static class TestOberver implements UpdateObserver {
        private final CountDownLatch latch;
        List<Checksum> observerChecksums;
        volatile Checksum checksum;

        public TestOberver(CountDownLatch latch, List<Checksum> observerChecksums) {
            this.latch = latch;
            this.observerChecksums = observerChecksums;
        }

        @Override
        public void done(final Update pUpdate) {
            observerChecksums.add(pUpdate.getPrevious());
            observerChecksums.add(pUpdate.getCurrent());
            checksum = pUpdate.getCurrent();
            latch.countDown();
        }
    }

    private static final String FIRST_EXPECTED_SHA_256_HASH = "b0a0a864cf2eb7c20a25bfe12f4cddc6070809e5da8f5da226234a258d17d336";
    private static final String SECOND_EXPECTED_SHA_256_HASH = "6c0f8adc6aac283543b974b395a8f9bb61e837076b2118fb9fbec71e1540b28e";
    private final File testfile = new File("target", "testfile_01.txt");

    @SuppressWarnings("CanBeFinal")
    private ResourceProducer producer;

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

    protected abstract ResourceProducerFactory getFactory();

    @Before
    public void setup() throws IOException {
        producer = getFactory().create(2);
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
        try (final BufferedWriter writer = newBufferedWriter(testfile.toPath(), APPEND)) {
            writer.write(FIRST_EXPECTED_SHA_256_HASH);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void tearDown() throws IOException {
        producer.close();
        testfile.delete();
    }

    @Test
    public void verifyPathResource() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final List<Checksum> observerChecksums = Collections.synchronizedList(new ArrayList<>());
        final Resource resource = producer.create(SHA256, testfile.toPath());

        TestOberver observer = new TestOberver(latch, observerChecksums);
        resource.update(observer);
        latch.await();

        final Checksum firstChecksum = observer.checksum;
        latch = new CountDownLatch(1);

        appendToTestFile();
        observer = new TestOberver(latch, observerChecksums);
        resource.update(observer);
        latch.await();

        final Checksum secondChecksum = observer.checksum;
        assertEquals(FIRST_EXPECTED_SHA_256_HASH, firstChecksum.getHexValue());
        assertEquals(SECOND_EXPECTED_SHA_256_HASH, secondChecksum.getHexValue());

        //latch.await();
        assertEquals(4, observerChecksums.size());
        observerChecksums.sort(Comparator.comparing(Checksum::getTimestamp));

        assertEquals(FIRST_EXPECTED_SHA_256_HASH, observerChecksums.get(0).getHexValue());
        assertEquals(FIRST_EXPECTED_SHA_256_HASH, observerChecksums.get(1).getHexValue());
        assertEquals(FIRST_EXPECTED_SHA_256_HASH, observerChecksums.get(2).getHexValue());
        assertEquals(SECOND_EXPECTED_SHA_256_HASH, observerChecksums.get(3).getHexValue());
    }
}
