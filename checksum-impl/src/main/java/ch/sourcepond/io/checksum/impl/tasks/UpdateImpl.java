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
import ch.sourcepond.io.checksum.api.Update;

import static java.lang.String.format;

/**
 *
 */
class UpdateImpl implements Update {
    private final Checksum previous;
    private final Checksum current;
    private final Exception failureOrNull;

    UpdateImpl(final Checksum pPrevious, final Checksum pCurrent, final Exception pFailureOrNull) {
        previous = pPrevious;
        current = pCurrent;
        failureOrNull = pFailureOrNull;
    }

    @Override
    public Checksum getPrevious() {
        return previous;
    }

    @Override
    public Checksum getCurrent() {
        return current;
    }

    @Override
    public Throwable getFailureOrNull() {
        return failureOrNull;
    }

    @Override
    public boolean hasChanged() {
        return failureOrNull == null && !previous.equals(current);
    }

    public String toString() {
        final String s;
        if (failureOrNull == null) {
            s = format("Update[previous: %s, current: %s]", previous, current);
        } else {
            s = format("Update[previous: %s, current: %s, failure: %s]", previous, current, failureOrNull.getMessage());
        }
        return s;
    }
}
