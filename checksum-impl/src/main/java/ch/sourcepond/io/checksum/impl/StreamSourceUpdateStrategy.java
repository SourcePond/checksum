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
package ch.sourcepond.io.checksum.impl;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import ch.sourcepond.io.checksum.api.StreamSource;

/**
 * @author rolandhauser
 *
 */
final class StreamSourceUpdateStrategy extends BaseUpdateStrategy<StreamSource> {
	private static final Logger LOG = getLogger(StreamSourceUpdateStrategy.class);

	StreamSourceUpdateStrategy(final String pAlgorithm, final StreamSource pSource) throws NoSuchAlgorithmException {
		super(pAlgorithm, pSource);
	}

	@Override
	protected void doUpdate(final long pInterval, final TimeUnit pUnit) throws IOException {
		try (final DigestInputStream din = new DigestInputStream(getSource().openStream(), getTmpDigest())) {
			final byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
			int read = din.read(buf);

			while (!isCancelled()) {
				if (read == EOF) {
					wait(pInterval, pUnit);
					read = din.read(buf);
					if (read == EOF) {
						break;
					}
				}
				read = din.read(buf);
			}
		}
	}
}
