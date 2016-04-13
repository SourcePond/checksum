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

import static ch.sourcepond.io.checksum.impl.DigestHelper.performUpdate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import ch.sourcepond.io.checksum.api.StreamSource;

/**
 * @author rolandhauser
 *
 */
final class StreamSourceUpdateStrategy extends BaseUpdateStrategy<StreamSource> {

	StreamSourceUpdateStrategy(final String pAlgorithm, final StreamSource pSource) throws NoSuchAlgorithmException {
		super(pAlgorithm, pSource);
	}

	@Override
	protected void doUpdate() throws IOException {
		performUpdate(getTmpDigest(), this, getSource().openStream());
	}

}
