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

import java.util.concurrent.ScheduledExecutorService;

/**
 * Factory to create {@link ResourceProducer} instances. This interface is the entry-point to use the checksum API.
 */
public interface ResourceProducerFactory {

    /**
     * Creates a new {@link ResourceProducer} object.
     *
     * @param pConcurrency Specifies, how many threads the thread-pool should use.
     * @return New resource producer, never {@code null}
     * @throws IllegalArgumentException Thrown, if the concurrency specified is zero or negative.
     */
    ResourceProducer create(int pConcurrency);

    /**
     * Creates a new {@link ResourceProducer} object.
     *
     * @param pExecutor Specifies the thread-pool to be used by the resource producer.
     * @return New resource producer, never {@code null}
     * @throws NullPointerException Thrown, if the executor specified is {@code null}.
     */
    ResourceProducer create(ScheduledExecutorService pExecutor);
}
