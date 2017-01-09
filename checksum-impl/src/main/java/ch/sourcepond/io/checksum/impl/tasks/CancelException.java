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

/**
 * Exception to be thrown when an update process is cancelled
 * with {@link DataReader#cancel()}. This exception is for
 * internal use only.
 */
final class CancelException extends Exception {

    public CancelException() { }

    public CancelException(final InterruptedException e) {
        super(e.getMessage(), e);
    }
}
