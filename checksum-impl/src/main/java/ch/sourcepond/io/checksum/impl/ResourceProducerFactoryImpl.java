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

import ch.sourcepond.io.checksum.api.ResourceProducer;
import ch.sourcepond.io.checksum.api.ResourceProducerFactory;

import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * Default implementation of the {@link ResourceProducerFactory} interface.
 */
public class ResourceProducerFactoryImpl implements ResourceProducerFactory {

    @Override
    public ResourceProducer create(final int pConcurrency) {
        if (0 >= pConcurrency) {
            throw new IllegalArgumentException(String.format("Concurrency must be positive, illegal argument %d", pConcurrency));
        }
        return new ResourceProducerImpl(newScheduledThreadPool(pConcurrency));
    }
}
