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
package ch.sourcepond.io.checksum.impl.digest;

/**
 *
 */
public abstract class Digest<T> implements Cancellable {
	private final String algorithm;
	private final T source;
	private volatile boolean cancelled;

	/**
	 * @param pAlgorithm
	 */
	Digest(final String pAlgorithm, T pSource) {
		algorithm = pAlgorithm;
		source = pSource;
	}

	/**
	 * @return
	 */
	public T getSource() {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.io.checksum.impl.digest.Cancellable#isCancelled()
	 */
	@Override
	public final boolean isCancelled() {
		return cancelled;
	}

	/**
	 * @param pCancelled
	 */
	protected final void setCancelled(final boolean pCancelled) {
		cancelled = pCancelled;
	}

	/**
	 * 
	 */
	public final void cancel() {
		setCancelled(true);
	}

	/**
	 * @return
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	@Override
	protected final void finalize() throws Throwable {
		cancel();
	}
}
