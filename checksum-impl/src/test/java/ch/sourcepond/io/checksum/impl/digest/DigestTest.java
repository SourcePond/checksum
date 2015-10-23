package ch.sourcepond.io.checksum.impl.digest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

/**
 * @author rolandhauser
 *
 */
public class DigestTest {
	@SuppressWarnings("unchecked")
	private final Digest<Object> digest = mock(Digest.class);

	/**
	 * 
	 */
	@Test
	public void verifyCancel() {
		assertFalse(digest.isCancelled());
		digest.cancel();
		assertTrue(digest.isCancelled());
		digest.setCancelled(false);
		assertFalse(digest.isCancelled());
	}
}
