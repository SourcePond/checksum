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

import static java.lang.Thread.currentThread;
import static org.apache.commons.codec.binary.Hex.encodeHexString;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.sourcepond.io.checksum.ChecksumException;
import ch.sourcepond.io.checksum.PathChecksum;

/**
 *
 */
final class DefaultPathChecksum extends BaseChecksum implements PathChecksum, Runnable {
	private final Lock lock = new ReentrantLock();
	private final Condition calculationDone = lock.newCondition();
	private final PathDigester digester;
	private final Executor executor;
	private Throwable throwable;
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
	 * @see ch.sourcepond.io.checksum.impl.BaseChecksum#
	 * getValueUnsynchronized()
	 */
	@Override
	protected byte[] evaluateValue() {
		return value;
	}

	/**
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void awaitCalculation() throws ChecksumException {
		try {
			while (awaitCalculation) {
				calculationDone.await();
			}
		} catch (final InterruptedException e) {
			currentThread().interrupt();
			throw new ChecksumException(e.getMessage(), e);
		}

		if (throwable != null) {

			// Only throw an ordinary throwable if the caught throwable is NOT
			// an error.
			if (!(throwable instanceof Error)) {
				throw new ChecksumException(throwable.getMessage(), throwable);
			}
			throw new Error(throwable.getMessage(), throwable);
		}
	}

	/**
	 * @return
	 */
	@Override
	public String getAlgorithm() {
		return digester.getAlgorithm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.BaseChecksum#getValue()
	 */
	@Override
	public byte[] getValue() throws ChecksumException {
		lock.lock();
		try {
			awaitCalculation();
			return super.getValue();
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.BaseChecksum#getHexValue()
	 */
	@Override
	public String getHexValue() throws ChecksumException {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.PathChecksum#equalsPrevious()
	 */
	@Override
	public boolean equalsPrevious() throws ChecksumException {
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
	 * @see ch.sourcepond.io.checksum.PathChecksum#getPreviousValue()
	 */
	@Override
	public byte[] getPreviousValue() throws ChecksumException {
		lock.lock();
		try {
			awaitCalculation();
			return copyArray(previousValue);
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.PathChecksum#getPreviousHexValue()
	 */
	@Override
	public String getPreviousHexValue() throws ChecksumException {
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
	 * @see ch.sourcepond.io.checksum.PathChecksum#update()
	 */
	@Override
	public void update() throws ChecksumException {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		lock.lock();
		try {
			final byte[] newValue = digester.updateDigest();
			if (newValue != null) {
				previousValue = value;
				value = newValue;
			}
		} catch (final Throwable e) {
			throwable = e;
		} finally {
			awaitCalculation = false;
			calculationDone.signalAll();
			lock.unlock();
		}
	}

	@Override
	public void cancel() {
		digester.cancel();
	}
}
