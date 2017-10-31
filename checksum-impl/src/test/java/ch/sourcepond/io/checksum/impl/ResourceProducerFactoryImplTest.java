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
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

public class ResourceProducerFactoryImplTest {
    private final ResourceProducerFactoryImpl factory = new ResourceProducerFactoryImpl();
    private ResourceProducer p1;
    private ResourceProducer p2;

    @After
    public void tearDown() {
        close(p1);
        close(p2);
    }

    private void close(final ResourceProducer p) {
        if (p != null) {
            p.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeConcurrency() {
        factory.create(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroConcurrency() {
        factory.create(0);
    }

    @Test
    public void create() {
        ResourceProducer p1 = factory.create(1);
        assertNotNull(p1);
        ResourceProducer p2 = factory.create(1);
        assertNotNull(p2);
        assertNotSame(p1, p2);
    }
}
