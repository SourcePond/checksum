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
package ch.sourcepond.io.checksum.api;

import java.util.concurrent.TimeUnit;

/**
 * A calculation observer will be informed when the calculation of checksum
 * has been finished successfully. See {@link Resource#update(TimeUnit, long, CalculationObserver)}
 * for further information.
 */
@FunctionalInterface
public interface CalculationObserver {

    /**
     * Triggered when the checksum calculation of a resource has been
     * finished.
     *
     * @param pPrevious Previous checksum i.e. checksum which was valid before
     *                  the update was triggered, never {@code null}
     * @param pCurrent Current checksum i.e. the checksum which is valid now, never {@code null}
     */
    void done(Checksum pPrevious, Checksum pCurrent);
}
