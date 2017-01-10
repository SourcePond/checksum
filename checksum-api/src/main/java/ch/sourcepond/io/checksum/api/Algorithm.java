/*Copyright (C) 2016 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.io.checksum.api;

/**
 * Constants for algorithms described here:
 * <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest">MessageDigest Algorithms</a>
 */
public enum Algorithm {
	/**
	 * Constant for MD2 algorithm
	 */
	MD2("MD2"),

	/**
	 * Constant for MD5 algorithm.
	 */
	MD5("MD5"),

	/**
	 * Constant for SHA-1 algorithm.
	 */
	SHA1("SHA-1"),

	/**
	 * Constant for SHA-224 algorithm.
	 */
	SHA224("SHA-224"),

	/**
	 * Constant for SHA-256 algorithm.
	 */
	SHA256("SHA-256"),

	/**
	 * Constant for SHA-384 algorithm.
	 */
	SHA384("SHA-384"),

	/**
	 * Constant for SHA-512 algorithm.
	 */
	SHA512("SHA-512");

	private final String name;

	private Algorithm(final String pName) {
		name = pName;
	}

	@Override
	public String toString() {
		return name;
	}
}