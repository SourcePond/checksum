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
package ch.sourcepond.io.checksum.api;

import java.security.MessageDigest;
import java.time.Instant;

import static java.lang.Integer.toHexString;

/**
 * Abstracts a checksum based on a specific hashing algorithm (see
 * {@link MessageDigest} for further information) and provides the ability to
 * query the represented checksum value.
 */
public interface Checksum {

    /**
     * Returns the timestamp when the calculation of this checksum successfully completed.
     *
     * @return Timestamp, never {@code null}
     */
    Instant getTimestamp();

    /**
     * <p>
     * Gets the result of the latest completed calculation triggered through one
     * of the {@code update} methods on {@link Resource}.
     * </p>
     * <p>
     * <p>
     * If the latest calculation was successful, the checksum will be returned
     * as byte array. The length of the array depends on the used hashing
     * algorithm (see <a href=
     * "http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest">
     * MessageDigest Algorithms</a> for further information). Note: the returned
     * array is a copy i.e. changing the returned value will have no effect on
     * the internal state of this object.
     * </p>
     * <p>
     * <p>
     * If none of the {@code update} methods on {@link Checksum} has
     * ever been called an empty array will be returned.
     * </p>
     *
     * @return The calculated checksum as byte array, never {@code null}
     */
    byte[] toByteArray();

    /**
     * Returns the checksum as hex-string. See {@link #toByteArray()} for further
     * information.
     *
     * @return The calculated checksum as string, never {@code null}
     */
    String getHexValue();

    static String toHexString(final byte[] pDigest) {
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < pDigest.length; i++) {
            int temp = 0xFF & pDigest[i];
            String s = Integer.toHexString(temp);
            if (temp <= 0x0F) {
                b.append('0').append(s);
            } else {
                b.append(s);
            }
        }
        return b.toString();
    }
}
