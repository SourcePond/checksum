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
package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.Checksum;

import java.time.Instant;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.time.Instant.MIN;

/**
 *
 */
final class ResourceNotAvailable implements Future<Checksum> {
    static final ResourceNotAvailable RESOURCE_NOT_AVAILABLE = new ResourceNotAvailable();
    static final byte[] ARR = new byte[0];

    static final Checksum EMPTY = new Checksum() {

        @Override
        public Instant getTimestamp() {
            return MIN;
        }

        @Override
        public byte[] toByteArray() {
            return ARR;
        }

        @Override
        public String getHexValue() {
            return "";
        }
    };

    private ResourceNotAvailable() {
        // Singleton
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        // noop by default
        return false;
    }

    @Override
    public boolean isCancelled() {
        // never cancelled
        return false;
    }

    @Override
    public boolean isDone() {
        // Always done
        return true;
    }

    @Override
    public Checksum get() {
        return EMPTY;
    }

    @Override
    public Checksum get(final long timeout, final TimeUnit unit) {
        return EMPTY;
    }
}
