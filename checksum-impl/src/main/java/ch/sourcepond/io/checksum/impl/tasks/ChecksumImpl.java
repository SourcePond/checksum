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

import java.time.Instant;

/**
 * Default implementation of the {@link Checksum} interface.
 */
final class ChecksumImpl implements Checksum {
    private final Instant timestamp;
    private final byte[] value;
    private final String hexValue;

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public ChecksumImpl(final Instant pTimestamp, final byte[] pValue) {
        assert pTimestamp != null : "pTimestamp is null";
        timestamp = pTimestamp;
        value = pValue;
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < pValue.length; i++) {
            int temp = 0xFF & pValue[i];
            String s = Integer.toHexString(temp);
            if (temp <= 0x0F) {
                b.append('0').append(s);
            } else {
                b.append(s);
            }
        }
        hexValue = b.toString();
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return hexValue.equals(((ChecksumImpl) o).hexValue);
    }

    @Override
    public int hashCode() {
        return hexValue.hashCode();
    }

    @Override
    public byte[] toByteArray() {
        final byte[] copy = new byte[value.length];
        System.arraycopy(value, 0, copy, 0, copy.length);
        return value;
    }

    @Override
    public String getHexValue() {
        return hexValue;
    }

    @Override
    public String toString() {
        return getHexValue();
    }
}
