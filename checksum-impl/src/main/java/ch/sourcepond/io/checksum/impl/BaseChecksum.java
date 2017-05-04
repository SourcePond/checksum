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
package ch.sourcepond.io.checksum.impl;

import ch.sourcepond.io.checksum.api.Checksum;

import static java.lang.String.format;

/**
 *
 */
public abstract class BaseChecksum implements Checksum {

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Checksum)) {
            return false;
        }
        return getHexValue().equals(((Checksum) o).getHexValue());
    }

    @Override
    public final int hashCode() {
        return getHexValue().hashCode();
    }

    @Override
    public final String toString() {
        return format("%s[hexValue: %s, timestamp: %s]", getClass().getSimpleName(), getHexValue(), getTimestamp().toEpochMilli());
    }
}
