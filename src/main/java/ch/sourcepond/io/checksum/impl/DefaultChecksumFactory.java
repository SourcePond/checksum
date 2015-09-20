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

import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import ch.sourcepond.io.checksum.ChecksumBuilder;
import ch.sourcepond.io.checksum.ChecksumBuilderFactory;
import ch.sourcepond.io.checksum.impl.digest.DigestFactory;

/**
 * Default implementation of the {@link ChecksumBuilderFactory} interface.
 *
 */
@Named
@Singleton
public class DefaultChecksumFactory implements ChecksumBuilderFactory {
	/**
	 * 
	 */
	public static final int DEFAULT_BUFFER_SIZE = 8192;
	private final DigestFactory digestFactory;

	/**
	 * @param pDigestFactory
	 */
	@Inject
	public DefaultChecksumFactory(final DigestFactory pDigestFactory) {
		digestFactory = pDigestFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.io.checksum.ChecksumBuilderFactory#create(java.lang.String)
	 */
	@Override
	public ChecksumBuilder create(final String pAlgorithm) throws NoSuchAlgorithmException {
		return new DefaultChecksumBuilder(digestFactory, pAlgorithm);
	}
}
