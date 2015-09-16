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
package ch.sourcepond.utils.checksum.impl;

import static java.security.MessageDigest.getInstance;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;

import javax.inject.Named;
import javax.inject.Singleton;

import ch.sourcepond.utils.checksum.Checksum;
import ch.sourcepond.utils.checksum.ChecksumException;
import ch.sourcepond.utils.checksum.ChecksumFactory;
import ch.sourcepond.utils.checksum.PathChecksum;

/**
 * Default implementation of the {@link ChecksumFactory} interface.
 *
 */
@Named
@Singleton
public class DefaultChecksumFactory implements ChecksumFactory {
	/**
	 * 
	 */
	static final int DEFAULT_BUFFER_SIZE = 8192;

	/**
	 * 
	 */
	static final Executor DIRECT_EXECUTOR = new Executor() {

		@Override
		public void execute(final Runnable command) {
			command.run();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.utils.checksum.ChecksumFactory#create(java.io.InputStream,
	 * java.lang.String)
	 */
	@Override
	public Checksum create(final InputStream pInputStream, final String pAlgorithm)
			throws NoSuchAlgorithmException, IOException {
		final MessageDigest digest = getInstance(pAlgorithm);
		try (final DigestInputStream din = new DigestInputStream(pInputStream, digest)) {
			final byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
			int read = din.read(buf);
			while (read != -1) {
				read = din.read(buf);
			}
		}
		return new ImmutableChecksum(digest.digest(), pAlgorithm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.utils.checksum.ChecksumFactory#create(java.nio.file.Path,
	 * java.lang.String)
	 */
	@Override
	public PathChecksum create(final Path pPath, final String pAlgorithm)
			throws NoSuchAlgorithmException, ChecksumException {
		return create(pPath, pAlgorithm, DIRECT_EXECUTOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.utils.checksum.ChecksumFactory#create(java.nio.file.Path,
	 * java.lang.String, java.util.concurrent.Executor)
	 */
	@Override
	public PathChecksum create(final Path pPath, final String pAlgorithm, final Executor pExecutor)
			throws NoSuchAlgorithmException, ChecksumException {
		final PathChecksum chsm = new DefaultPathChecksum(new PathDigester(pAlgorithm, pPath), pExecutor);
		chsm.update();
		return chsm;
	}
}
