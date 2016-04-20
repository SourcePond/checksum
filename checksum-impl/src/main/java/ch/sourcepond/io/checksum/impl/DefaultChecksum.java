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
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import ch.sourcepond.io.checksum.api.Checksum;
import ch.sourcepond.io.checksum.api.ChecksumException;
import ch.sourcepond.io.checksum.api.UpdateObserver;

/**
 * Default implementation of the {@link Checksum} interface.
 * 
 * @author rolandhauser
 *
 */
final class DefaultChecksum implements Checksum {

	/**
	 *
	 */
	private final class UpdateTask implements Runnable {
		private final long interval;
		private final TimeUnit unit;

		public UpdateTask(final long pInterval, final TimeUnit pUnit) {
			interval = pInterval;
			unit = pUnit;
		}

		@Override
		public void run() {
			synchronized (strategy) {
				// Do the calculation outside the mutex
				try {
					strategy.update(interval, unit);

					// Finally, assign the new digester values if not
					// cancelled
					// i.e. a
					// null-value indicates that the calculation has been
					// cancelled.
					final byte[] newValue = strategy.digest();
					if (newValue != null) {
						previousValue = value;
						value = newValue;
						informObservers(SUCCESS);
					} else {
						informObservers(CANCELLED);
					}
				} catch (final Throwable e) {
					// Only throw an ordinary throwable if the caught
					// throwable is
					// NOT an error.
					if (e instanceof Error) {
						throwable = e;
					} else {
						final ChecksumException ex = new ChecksumException(e.getMessage(), e);
						informObservers(FAILURE, ex);
						throwable = ex;
					}
				} finally {
					updating = nextTask != null;
					try {
						if (updating) {
							updateExecutor.execute(nextTask);
						}
					} catch (final RejectedExecutionException e) {
						LOG.warn(e.getMessage(), e);
					} finally {
						nextTask = null;
						strategy.notifyAll();
					}
				}
			}
		}
	}

	private static final Logger LOG = getLogger(DefaultChecksum.class);
	static final byte[] INITIAL = new byte[0];
	/**
	 * Set of observers; this object is used as observer-monitor
	 */
	private final Set<UpdateObserver> observers = new HashSet<>();
	/**
	 * Strategy object to the actual digester work; this object is used as
	 * update-monitor.
	 */
	private final UpdateStrategy strategy;
	private final Executor updateExecutor;
	private final Executor observerExecutor;
	private UpdateTask nextTask;
	private boolean updating;
	private Throwable throwable;
	private byte[] previousValue = INITIAL;
	private byte[] value = INITIAL;

	/**
	 * @param pStrategy
	 * @param pUpdateExecutor
	 */
	DefaultChecksum(final UpdateStrategy pStrategy, final Executor pUpdateExecutor, final Executor pListenerExecutor) {
		strategy = pStrategy;
		updateExecutor = pUpdateExecutor;
		observerExecutor = pListenerExecutor;
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
		try {
			while (updating) {
				strategy.wait();
			}
		} catch (final InterruptedException e) {
			currentThread().interrupt();
			throw new ChecksumException(e.getMessage(), e);
		}

		if (throwable != null) {
			if (throwable instanceof Error) {
				throw (Error) throwable;
			}
			throw (ChecksumException) throwable;
		}
	}

	private void informObservers(final ObserverCallback pCallback) {
		informObservers(pCallback, null);
	}

	private void informObservers(final ObserverCallback pCallback, final ChecksumException pFailureOrNull) {
		synchronized (observers) {
			for (final UpdateObserver observer : observers) {
				observerExecutor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							pCallback.inform(observer, DefaultChecksum.this, pFailureOrNull);
						} catch (final Exception th) {
							LOG.warn(format("Listener %s has thrown an exception! See stacktrace"), th);
						}
					}
				});
			}
		}
	}

	@Override
	public Checksum cancel() {
		synchronized (strategy) {
			strategy.cancel();
		}
		return this;
	}

	@Override
	public Checksum update() {
		return update(0, MILLISECONDS);
	}

	@Override
	public Checksum update(final long pIntervalInMilliseconds) {
		return update(pIntervalInMilliseconds, MILLISECONDS);
	}

	@Override
	public Checksum update(final long pInterval, final TimeUnit pUnit) {
		synchronized (strategy) {
			// An update is only scheduled, if
			// a) no update is currently running
			// b) no update is waiting for execution.
			if (!updating) {
				updateExecutor.execute(new UpdateTask(pInterval, pUnit));
				updating = true;
			} else if (nextTask == null) {
				nextTask = new UpdateTask(pInterval, pUnit);
			}
		}
		return this;
	}

	@Override
	public boolean isUpdating() {
		synchronized (strategy) {
			return updating;
		}
	}

	@Override
	public String getAlgorithm() {
		// Final value, no synchronization necessary
		return strategy.getAlgorithm();
	}

	@Override
	public byte[] getValue() throws ChecksumException {
		synchronized (strategy) {
			awaitCalculation();
			return copyArray(value);
		}
	}

	@Override
	public String getHexValue() throws ChecksumException {
		synchronized (strategy) {
			awaitCalculation();
			return encodeHexString(value);
		}
	}

	@Override
	public boolean equalsPrevious() throws ChecksumException {
		synchronized (strategy) {
			awaitCalculation();
			return Arrays.equals(previousValue, value);
		}
	}

	@Override
	public byte[] getPreviousValue() throws ChecksumException {
		synchronized (strategy) {
			awaitCalculation();
			return copyArray(previousValue);
		}
	}

	@Override
	public String getPreviousHexValue() throws ChecksumException {
		synchronized (strategy) {
			awaitCalculation();
			return encodeHexString(previousValue);
		}
	}

	@Override
	public Checksum addUpdateObserver(final UpdateObserver pObserver) {
		synchronized (observers) {
			observers.add(pObserver);
		}
		LOG.debug("Added observer {}", pObserver);
		return this;
	}

	@Override
	public Checksum removeUpdateObserver(final UpdateObserver pObserver) {
		synchronized (observers) {
			observers.remove(pObserver);
		}
		LOG.debug("Removed observer {}", pObserver);
		return this;
	}

}
