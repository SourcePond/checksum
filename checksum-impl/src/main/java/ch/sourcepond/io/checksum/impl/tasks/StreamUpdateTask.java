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

import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.resources.Observable;
import ch.sourcepond.io.checksum.impl.pools.Pool;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Created by rolandhauser on 06.01.17.
 */
final class StreamUpdateTask extends UpdateTask<StreamSource> {

    StreamUpdateTask(final Pool<MessageDigest> pDigesterPool,
                     final Observable pResource,
                     final DataReader pReader) {
        super(pDigesterPool, pResource, pReader);
    }

    @Override
    void updateDigest(final MessageDigest pDigest) throws InterruptedException, IOException {
        try (final InputStream in = resource.getSource().openStream()) {
            final byte[] buffer = new byte[1024];
            reader.read(() -> in.read(buffer), readBytes -> pDigest.update(buffer, 0, readBytes));
        }
    }
}
