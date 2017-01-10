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
package ch.sourcepond.io.checksum.impl.tasks;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.interrupted;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Utility class for uniting timeout and cancel handling.
 */
class DataReader {

    @FunctionalInterface
    static interface Reader {

        int read() throws IOException;
    }

    @FunctionalInterface
    static interface Updater {

        void update(int pReadBytes);
    }

    private static final Logger LOG = getLogger(DataReader.class);
    private final TimeUnit unit;
    private final long interval;
    private int iterations;

    public DataReader(final TimeUnit pUnit, final long pInterval) {
        unit = pUnit;
        interval = pInterval;
    }

    private boolean readData(final Reader pReader, final Updater pUpdater) throws InterruptedException, IOException {
        if (interrupted()) {
            throw new InterruptedException();
        }

        final int readBytes = pReader.read();
        if (readBytes == -1) {
            // Increment iterations if currently no more data is available.
            iterations++;
        } else {
            // If more data is available, reset iterations to zero.
            if (iterations > 0) {
                iterations = 0;
            }
            pUpdater.update(readBytes);
        }

        return readBytes != -1 || 1 >= iterations;
    }

    void read(final Reader pReader, final Updater pUpdater) throws InterruptedException, IOException {
        while (readData(pReader, pUpdater)) {
            // If currently no data is available wait for a specific
            // amount of time.
            delay();
        }
    }

    private void delay() throws InterruptedException {
        if (interval > 0) {
            try {
                unit.sleep(interval);
            } catch (final InterruptedException e) {
                currentThread().interrupt();
                throw e;
            }
        }
    }
}
