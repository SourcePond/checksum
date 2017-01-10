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

/**
 * Created by rolandhauser on 05.01.17.
 */
final class ChecksumImpl implements Checksum {
    private final byte[] value;
    private final String hexValue;

    public ChecksumImpl(final byte[] value) {
        this.value = value;
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            int temp = 0xFF & value[i];
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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ChecksumImpl checksum = (ChecksumImpl) o;

        return hexValue.equals(checksum.hexValue);
    }

    @Override
    public int hashCode() {
        return hexValue.hashCode();
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public String getHexValue() {
        return hexValue;
    }
}
