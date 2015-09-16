package ch.sourcepond.utils.checksum.impl;

/**
 * @author rolandhauser
 *
 */
public class ImmutableChecksumTest extends BaseChecksumTest<ImmutableChecksum> {

	@Override
	protected ImmutableChecksum createChecksum() {
		return new ImmutableChecksum(VALUE, ANY_ALGORITHM);
	}

}
