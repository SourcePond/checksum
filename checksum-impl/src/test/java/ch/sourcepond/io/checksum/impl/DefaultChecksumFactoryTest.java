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

import java.io.InputStream;
import java.nio.file.Path;

import org.junit.Test;

import ch.sourcepond.io.checksum.ChecksumBuilderFactory;
import ch.sourcepond.io.checksum.impl.digest.DefaultDigestFactory;

/**
 * @author rolandhauser
 *
 */
public class DefaultChecksumFactoryTest extends ChecksumFactoryTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.ChecksumFactoryTest#getFactory()
	 */
	@Override
	protected ChecksumBuilderFactory getBuilderFactory() {
		return new DefaultChecksumBuilderFactory(new DefaultDigestFactory());
	}

	/**
	 * 
	 */
	@Test(expected = NullPointerException.class)
	public void verifyCreateOneTimeNullInputStream() throws Exception {
		builder.create((InputStream) null);
	}

	/**
	 * 
	 */
	@Test(expected = NullPointerException.class)
	public void verifyCreateNullPath() throws Exception {
		builder.create((Path) null);
	}
}
