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
package ch.sourcepond.io.checksum.impl.resources;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 *
 */
public class URLStreamSourceTest {
    private URLStreamSource source;

    @Before
    public void setup() {
        source = new URLStreamSource(getClass().getResource("/testfile_01.txt"));
    }

    @Test
    public void openStream() throws Exception {
        try (final InputStream in = source.openStream()) {
            assertNotNull(in);
        }
    }
}
