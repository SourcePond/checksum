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
import ch.sourcepond.io.checksum.api.Update;
import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.impl.BaseChecksum;

import java.time.Instant;

import static java.lang.Thread.currentThread;
import static java.time.Instant.MIN;

/**
 *
 */
final class InitialChecksum extends BaseChecksum implements UpdateObserver {
    private volatile Instant timestamp;
    private volatile byte[] bytes;
    private volatile String hexValue;

    private synchronized void awaitCalculation() {
        try {
            while (timestamp == null) {
                wait();
            }
        } catch (final InterruptedException e) {
            currentThread().interrupt();
            internalInitDefaults();
        }
    }

    private void internalInitDefaults() {
        timestamp = MIN;
        bytes = new byte[0];
        hexValue = "";
    }

    synchronized void initDefaults() {
        internalInitDefaults();
        notifyAll();
    }

    @Override
    public Instant getTimestamp() {
        awaitCalculation();
        return timestamp;
    }

    @Override
    public byte[] toByteArray() {
        awaitCalculation();
        return bytes;
    }

    @Override
    public String getHexValue() {
        awaitCalculation();
        return hexValue;
    }

    @Override
    public synchronized void done(final Update pUpdate) {
        try {
            final Checksum current = pUpdate.getCurrent();
            timestamp = current.getTimestamp();
            bytes = current.toByteArray();
            hexValue = current.getHexValue();
        } finally {
            notifyAll();
        }
    }
}
