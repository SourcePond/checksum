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

import static org.apache.commons.codec.binary.Hex.encodeHexString;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.sourcepond.utils.checksum.PathChecksum;

/**
 *
 */
final class DefaultPathChecksum extends BaseChecksum implements PathChecksum, Runnable {
	private static final byte[] INITIAL = new byte[0];
	private final Lock lock = new ReentrantLock();
	private final Condition calculationDone = lock.newCondition();
	private final PathDigester digester;
	private final Executor executor;
	private IOException exception;
	private byte[] previousValue = INITIAL;
	private byte[] value = INITIAL;
	private boolean awaitCalculation;

	/**
	 * @param pDigest
	 * @param pPath
	 */
	DefaultPathChecksum(final PathDigester pDigester, final Executor pExecutor) {
		digester = pDigester;
		executor = pExecutor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.utils.checksum.impl.BaseChecksum#
	 * getValueUnsynchronized()
	 */
	@Override
	protected byte[] getValueUnsynchronized() {
		return value;
	}

	/**
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void awaitCalculation() throws IOException, InterruptedException {
		while (awaitCalculation) {
			calculationDone.await();
		}

		if (exception != null) {
			throw exception;
		}
	}

	/**
	 * @return
	 */
	@Override
	public String getAlgorithm() {
		return digester.getAlgorithm();
	}

	@Override
	public byte[] getValue() throws IOException, InterruptedException {
		lock.lock();
		try {
			awaitCalculation();
			return super.getValue();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String getHexValue() throws IOException, InterruptedException {
		lock.lock();
		try {
			awaitCalculation();
			return super.getHexValue();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @return
	 */
	@Override
	public Path getPath() {
		return digester.getPath();
	}

	@Override
	public boolean equalsPrevious() throws IOException, InterruptedException {
		lock.lock();
		try {
			awaitCalculation();
			return Arrays.equals(previousValue, value);
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.utils.checksum.PathChecksum#getPreviousValue()
	 */
	@Override
	public byte[] getPreviousValue() throws IOException, InterruptedException {
		lock.lock();
		try {
			awaitCalculation();
			return previousValue;
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.utils.checksum.PathChecksum#getPreviousHexValue()
	 */
	@Override
	public String getPreviousHexValue() throws IOException, InterruptedException {
		lock.lock();
		try {
			awaitCalculation();
			return encodeHexString(previousValue);
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.utils.checksum.PathChecksum#update()
	 */
	@Override
	public void update() throws IOException, InterruptedException {
		lock.lock();
		try {
			// If a previous update is running we need to wait until it's done
			awaitCalculation();
			awaitCalculation = true;
		} finally {
			lock.unlock();
		}

		executor.execute(this);
	}

	@Override
	public void run() {
		lock.lock();
		try {
			final byte[] newValue = digester.updateDigest();
			previousValue = value;
			value = newValue;
		} catch (final IOException e) {
			exception = e;
		} finally {
			awaitCalculation = false;
			calculationDone.signalAll();
			lock.unlock();
		}
	}
}
