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

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.ChecksumException;
import ch.sourcepond.io.checksum.api.UpdateObserver;

interface ObserverCallback {
	ObserverCallback SUCCESS = new ObserverCallback() {

		@Override
		public void inform(final UpdateObserver pObserver, final Checksum pChecksum, final ChecksumException pFailure)
				throws ChecksumException {
			pObserver.success(pChecksum);
		}
	};
	ObserverCallback CANCELLED = new ObserverCallback() {

		@Override
		public void inform(final UpdateObserver pObserver, final Checksum pChecksum, final ChecksumException pFailure)
				throws ChecksumException {
			pObserver.cancel(pChecksum);
		}
	};
	ObserverCallback FAILURE = new ObserverCallback() {

		@Override
		public void inform(final UpdateObserver pObserver, final Checksum pChecksum, final ChecksumException pFailure)
				throws ChecksumException {
			pObserver.failure(pChecksum, pFailure);
		}
	};

	void inform(UpdateObserver pObserver, Checksum pChecksum, ChecksumException pFailure) throws ChecksumException;
}