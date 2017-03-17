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
package ch.sourcepond.io.checksum.api;

/**
 * Holds information about a checksum update which has been caused through one of the
 * {@code update} methods on {@link Resource}.
 */
public interface Update {

    /**
     * Returns the previous checksum i.e. the one which has been replaced with
     * a new value.
     *
     * @return Previous checksum, never {@code null}
     */
    Checksum getPrevious();

    /**
     * Returns the current checksum i.e. the one which has replaced the previous
     * value. If a failure occurred during update (see {@link #getFailureOrNull()}) this method returns
     * the same value as {@link #getPrevious()}.
     *
     * @return The current checksum, never {@code null}
     */
    Checksum getCurrent();

    /**
     * If during update a failure occurred, the {@link Throwable} can be retrieved through this method. In case
     * the updated was successful, {@code null} is returned.
     *
     * @return Failure occurred during update or {@code} if successful
     */
    Throwable getFailureOrNull();

    /**
     * Indicates whether the previous and current checksums are different. If the previous checksum is <em>not</em>
     * equal to the current value {@code true} is returned. If the previous checksum is equal to the current value
     * {@code false} is returned. If a failure occurred during update {@code false} is returned because the current
     * checksum won't be updated (this happens only if the update succeeds).
     *
     * @return {@code true} if the checksums are different, {@code false} otherwise
     */
    boolean hasChanged();
}
