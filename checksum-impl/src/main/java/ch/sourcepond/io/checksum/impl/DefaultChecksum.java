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

import static ch.sourcepond.io.checksum.impl.ObserverCallback.CANCELLED;
import static ch.sourcepond.io.checksum.impl.ObserverCallback.FAILURE;
import static ch.sourcepond.io.checksum.impl.ObserverCallback.SUCCESS;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.ChecksumException;
import ch.sourcepond.io.checksum.api.UpdateObserver;
import ch.sourcepond.io.checksum.api.UpdateableChecksum;

/**
 * Default implementation of the {@link Checksum} interface.
 * 
 * @author rolandhauser
 *
 */
class DefaultChecksum implements UpdateableChecksum {

	/**
	 *
	 */
	private class UpdateTask implements Runnable {
		private final long interval;
		private final TimeUnit unit;

		public UpdateTask(final long pInterval, final TimeUnit pUnit) {
			interval = pInterval;
			unit = pUnit;
		}

		@Override
		public void run() {
			// Do the calculation outside the mutex
			Throwable th = null;
			try {
				strategy.update(interval, unit);
			} catch (final Throwable e) {
				th = e;
			}

			// Do any assignment inside the mutex
			updateLock.lock();
			try {
				if (th != null) {
					throwable = th;
				}
			} finally {
				updating = nextTask != null;
				try {
					if (updating) {
						executor.execute(nextTask);
					}
				} finally {
					nextTask = null;
					updateDone.signalAll();
					updateLock.unlock();
				}
			}
		}
	}

	private static final Logger LOG = getLogger(DefaultChecksum.class);
	static final byte[] INITIAL = new byte[0];
	private final Lock observerLock = new ReentrantLock();
	private final Lock updateLock = new ReentrantLock();
	private final Condition updateDone = updateLock.newCondition();
	private final Set<UpdateObserver> observers = new HashSet<>();
	private final UpdateStrategy strategy;
	private final Executor executor;
	private UpdateTask nextTask;
	private boolean updating;
	private Throwable throwable;
	private byte[] previousValue = INITIAL;
	private byte[] value = INITIAL;

	/**
	 * @param pStrategy
	 * @param pExecutor
	 */
	DefaultChecksum(final UpdateStrategy pStrategy, final Executor pExecutor) {
		strategy = pStrategy;
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
	private void awaitCalculation() throws ChecksumException {
		final boolean needsDigest = updating;
		try {
			while (updating) {
				updateDone.await();
			}
		} catch (final InterruptedException e) {
			currentThread().interrupt();
			throw new ChecksumException(e.getMessage(), e);
		}

		if (throwable != null) {
			try {
				// Only throw an ordinary throwable if the caught throwable is
				// NOT an error.
				if (!(throwable instanceof Error)) {
					final ChecksumException ex = new ChecksumException(throwable.getMessage(), throwable);
					informObservers(FAILURE, ex);
					throw ex;
				}
				throw new Error(throwable.getMessage(), throwable);
			} finally {
				// In any case set the throwable to null
				throwable = null;
			}
		} else if (needsDigest) {
			// Finally, assign the new digester values if not cancelled i.e. a
			// null-value indicates that the calculation has been cancelled.
			final byte[] newValue = strategy.digest();
			if (newValue != null) {
				previousValue = value;
				value = newValue;
				informObservers(SUCCESS);
			} else {
				informObservers(CANCELLED);
			}
		}
	}

	private void informObservers(final ObserverCallback pCallback) {
		informObservers(pCallback, null);
	}

	private void informObservers(final ObserverCallback pCallback, final ChecksumException pFailureOrNull) {
		observerLock.lock();
		try {
			for (final UpdateObserver observer : observers) {
				try {
					pCallback.inform(observer, this, pFailureOrNull);
				} catch (final Exception th) {
					LOG.warn(format("Listener %s has thrown an exception! See stacktrace"), th);
				}
			}
		} finally {
			observerLock.unlock();
		}
	}

	@Override
	public UpdateableChecksum cancel() {
		updateLock.lock();
		try {
			strategy.cancel();
		} finally {
			updateLock.unlock();
		}
		return this;
	}

	@Override
	public UpdateableChecksum update() {
		return update(0, MILLISECONDS);
	}

	@Override
	public UpdateableChecksum update(final long pIntervalInMilliseconds) {
		return update(pIntervalInMilliseconds, MILLISECONDS);
	}

	@Override
	public UpdateableChecksum update(final long pInterval, final TimeUnit pUnit) {
		updateLock.lock();
		try {
			if (observerLock.tryLock()) {
				try {
					// An update is only scheduled, if
					// a) no update is currently running
					// b) no update is waiting for execution.
					if (!updating) {
						executor.execute(new UpdateTask(pInterval, pUnit));
						updating = true;
					} else if (nextTask == null) {
						nextTask = new UpdateTask(pInterval, pUnit);
					}
				} finally {
					observerLock.unlock();
				}
			} else {
				// This could happen if in an UpdateObserver the update method
				// is called; this would cause a dead-lock.
				throw new IllegalStateException("Observer-lock is already held by another thread!");
			}
		} finally {
			updateLock.unlock();
		}
		return this;
	}

	@Override
	public boolean isUpdating() {
		updateLock.lock();
		try {
			return updating;
		} finally {
			updateLock.unlock();
		}
	}

	@Override
	public String getAlgorithm() {
		return strategy.getAlgorithm();
	}

	@Override
	public byte[] getValue() throws ChecksumException {
		updateLock.lock();
		try {
			awaitCalculation();
			return copyArray(value);
		} finally {
			updateLock.unlock();
		}
	}

	@Override
	public String getHexValue() throws ChecksumException {
		updateLock.lock();
		try {
			awaitCalculation();
			return encodeHexString(value);
		} finally {
			updateLock.unlock();
		}
	}

	@Override
	public boolean equalsPrevious() throws ChecksumException {
		updateLock.lock();
		try {
			awaitCalculation();
			return Arrays.equals(previousValue, value);
		} finally {
			updateLock.unlock();
		}
	}

	@Override
	public byte[] getPreviousValue() throws ChecksumException {
		updateLock.lock();
		try {
			awaitCalculation();
			return copyArray(previousValue);
		} finally {
			updateLock.unlock();
		}
	}

	@Override
	public String getPreviousHexValue() throws ChecksumException {
		updateLock.lock();
		try {
			awaitCalculation();
			return encodeHexString(previousValue);
		} finally {
			updateLock.unlock();
		}
	}

	@Override
	public UpdateableChecksum addUpdateObserver(final UpdateObserver pObserver) {
		observerLock.lock();
		try {
			observers.add(pObserver);
		} finally {
			observerLock.unlock();
		}
		LOG.debug("Added observer {}", pObserver);
		return this;
	}

	@Override
	public UpdateableChecksum removeUpdateObserver(final UpdateObserver pObserver) {
		observerLock.lock();
		try {
			observers.remove(pObserver);
		} finally {
			observerLock.unlock();
		}
		LOG.debug("Removed observer {}", pObserver);
		return this;
	}

}
