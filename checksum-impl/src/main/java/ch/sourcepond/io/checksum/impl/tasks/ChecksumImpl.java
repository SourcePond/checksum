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

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.impl.BaseChecksum;

import java.time.Instant;

import static ch.sourcepond.io.checksum.api.Checksum.toHexString;
import static java.lang.System.arraycopy;

/**
 * Default implementation of the {@link Checksum} interface.
 */
final class ChecksumImpl extends BaseChecksum {
    private final Instant timestamp;
    private final byte[] value;
    private final String hexValue;

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public ChecksumImpl(final Instant pTimestamp, final byte[] pValue) {
        assert pTimestamp != null : "pTimestamp is null";
        timestamp = pTimestamp;
        value = pValue;
        hexValue = toHexString(pValue);
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public byte[] toByteArray() {
        final byte[] copy = new byte[value.length];
        arraycopy(value, 0, copy, 0, copy.length);
        return value;
    }

    @Override
    public String getHexValue() {
        return hexValue;
    }
}
