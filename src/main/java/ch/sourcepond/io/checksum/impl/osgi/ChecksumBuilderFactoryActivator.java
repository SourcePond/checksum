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
package ch.sourcepond.io.checksum.impl.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ch.sourcepond.io.checksum.ChecksumBuilderFactory;
import ch.sourcepond.io.checksum.impl.DefaultChecksumFactory;
import ch.sourcepond.io.checksum.impl.digest.DefaultDigestFactory;

/**
 *
 */
public class ChecksumBuilderFactoryActivator implements BundleActivator {
	private final ChecksumBuilderFactory factory;

	/**
	 * 
	 */
	public ChecksumBuilderFactoryActivator() {
		this(new DefaultChecksumFactory(new DefaultDigestFactory()));
	}

	/**
	 * @param pFactory
	 */
	ChecksumBuilderFactoryActivator(final ChecksumBuilderFactory pFactory) {
		factory = pFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		context.registerService(ChecksumBuilderFactory.class, factory, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		// noop; service unregistration is done by the framework
	}

}
