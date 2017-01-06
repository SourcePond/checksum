package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.Checksum;

/**
 * Created by rolandhauser on 05.01.17.
 */
final class ChecksumImpl implements Checksum {
    private final byte[] value;
    private final String hexValue;

    public ChecksumImpl(final byte[] value) {
        this.value = value;
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            int temp = 0xFF & value[i];
            String s = Integer.toHexString(temp);
            if (temp <= 0x0F) {
                b.append('0').append(s);
            } else {
                b.append(s);
            }
        }
        hexValue = b.toString();
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public String getHexValue() {
        return hexValue;
    }
}
