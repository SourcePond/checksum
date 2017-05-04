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
package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.impl.BaseChecksum;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static ch.sourcepond.io.checksum.impl.resources.ResourceNotAvailable.ARR;
import static java.lang.Thread.currentThread;
import static java.time.Instant.MIN;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 */
final class InitialChecksum extends BaseChecksum {

    @FunctionalInterface
    private interface ValueOf<T> {

        T get() throws ExecutionException, InterruptedException;
    }

    private static final Logger LOG = getLogger(InitialChecksum.class);
    private final Future<Checksum> future;

    InitialChecksum(final Future<Checksum> pFuture) {
        future = pFuture;
    }

    private static <T> T valueOf(final ValueOf<T> pSupplier, T pDefault) {
        T value;
        try {
            value = pSupplier.get();
        } catch (final ExecutionException e) {
            LOG.error(e.getMessage(), e);
            value = pDefault;
        } catch (final InterruptedException e) {
            currentThread().interrupt();
            value = pDefault;
        }
        return value;
    }

    @Override
    public Instant getTimestamp() {
        return valueOf(() -> future.get().getTimestamp(), MIN);
    }

    @Override
    public byte[] toByteArray() {
        return valueOf(() -> future.get().toByteArray(), ARR);
    }

    @Override
    public String getHexValue() {
        return valueOf(() -> future.get().getHexValue(), "");
    }
}
