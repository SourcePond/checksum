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

import static java.lang.System.arraycopy;
import static java.lang.Thread.currentThread;
import static org.apache.commons.codec.binary.Hex.encodeHexString;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.ChecksumException;

/**
 * Default implementation of the {@link Checksum} interface.
 * 
 * @author rolandhauser
 *
 */
class DefaultChecksum implements Checksum, Runnable {
	static final byte[] INITIAL = new byte[0];
	private final Lock lock = new ReentrantLock();
	private final Condition updateDone = lock.newCondition();
	private final UpdateStrategy<?> digester;
	private final Executor executor;
	private int triggeredUpdates;
	private Throwable throwable;
	private byte[] previousValue = INITIAL;
	private byte[] value = INITIAL;

	/**
	 * @param pDigester
	 * @param pExecutor
	 */
	DefaultChecksum(final UpdateStrategy<?> pDigester, final Executor pExecutor) {
		digester = pDigester;
		executor = pExecutor;
	}

	/**
	 * Creates a copy of the array specified.
	 * 
	 * @param pOriginal
	 *            Original array, must not be {@code null}
	 * @return Copied array, never {@code null}
	 */
	private static byte[] copyArray(final byte[] pOriginal) {
		final byte[] copy = new byte[pOriginal.length];
		arraycopy(pOriginal, 0, copy, 0, pOriginal.length);
		return copy;
	}

	/**
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void awaitCalculation() throws InterruptedException, ChecksumException {
		try {
			while (triggeredUpdates > 0) {
				updateDone.await();
			}
		} catch (final InterruptedException e) {
			currentThread().interrupt();
			throw e;
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

	@Override
	public Checksum cancel() {
		lock.lock();
		try {
			digester.cancel();
			return this;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Checksum update() {
		lock.lock();
		try {
			if (2 >= triggeredUpdates && ++triggeredUpdates == 1) {
				executor.execute(this);
			}
			return this;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isUpdating() {
		lock.lock();
		try {
			return triggeredUpdates > 0;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String getAlgorithm() {
		return digester.getAlgorithm();
	}

	@Override
	public byte[] getValue() throws InterruptedException, ChecksumException {
		lock.lock();
		try {
			awaitCalculation();
			return copyArray(value);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String getHexValue() throws InterruptedException, ChecksumException {
		lock.lock();
		try {
			awaitCalculation();
			return encodeHexString(value);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean equalsPrevious() throws InterruptedException, ChecksumException {
		lock.lock();
		try {
			awaitCalculation();
			return Arrays.equals(previousValue, value);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public byte[] getPreviousValue() throws InterruptedException, ChecksumException {
		lock.lock();
		try {
			awaitCalculation();
			return copyArray(previousValue);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String getPreviousHexValue() throws InterruptedException, ChecksumException {
		lock.lock();
		try {
			awaitCalculation();
			return encodeHexString(previousValue);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void run() {
		// Do the calculation outside the mutex
		byte[] newValue = null;
		Throwable th = null;
		try {
			newValue = digester.update();
		} catch (final Throwable e) {
			th = e;
		}

		// Do any assignment inside the mutex
		lock.lock();
		try {
			if (th != null) {
				throwable = th;
			}
			if (newValue != null) {
				previousValue = value;
				value = newValue;
			}
		} finally {
			if (--triggeredUpdates == 1) {
				executor.execute(this);
			}

			updateDone.signalAll();
			lock.unlock();
		}
	}
}
