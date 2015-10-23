package ch.sourcepond.io.checksum.impl.digest;

import java.io.IOException;

import org.junit.Test;

/**
 * @author rolandhauser
 *
 */
public class UpdatableDigestTest {

	/**
	 * 
	 */
	@Test(expected = InstantiationError.class)
	public void verifyThrowErrorIfAlgorithmIsUnknown() {
		new UpdatableDigest<Object>("Unknown", new Object()) {

			@Override
			public byte[] updateDigest() throws IOException {
				return null;
			}
		};
	}
}
