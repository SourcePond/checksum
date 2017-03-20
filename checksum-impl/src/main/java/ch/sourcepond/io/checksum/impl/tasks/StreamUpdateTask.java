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
import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.impl.pools.DigesterPool;
import ch.sourcepond.io.checksum.impl.resources.BaseResource;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 *
 */
final class StreamUpdateTask extends UpdateTask<StreamSource> {
    private final InputStream in;

    StreamUpdateTask(final DigesterPool pDigesterPool,
                     final ResultFuture pFuture,
                     final UpdateObserver pObserver,
                     final BaseResource<StreamSource> pResource,
                     final DataReader pReader) throws IOException {
        super(pDigesterPool, pFuture, pObserver, pResource, pReader);
        in = pResource.getSource().openStream();
    }

    @Override
    void updateDigest(final MessageDigest pDigest) throws InterruptedException, IOException {
        final byte[] buffer = new byte[1024];
        reader.read(() -> in.read(buffer), readBytes -> pDigest.update(buffer, 0, readBytes));
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
