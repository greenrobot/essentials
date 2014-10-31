package de.greenrobot.common.hash;

import org.junit.Assert;
import org.junit.Test;

public class FNV64Test extends  AbstractChecksumTest {
    private final static byte[] INPUT64_ZERO = {(byte) 0xd5, 0x6b, (byte) 0xb9, 0x53, 0x42, (byte) 0x87, 0x08, 0x36};

    public FNV64Test() {
        super(new FNV64());
    }

    @Test
    public void testFnv64UpdateZeroHash() {
        for (int b : INPUT64_ZERO) {
            checksum.update(b);
        }
        Assert.assertEquals(0, checksum.getValue());
    }

    @Test
    public void testFnv64UpdateBytesZeroHash() {
        checksum.update(INPUT64_ZERO, 0, INPUT64_ZERO.length);
        Assert.assertEquals(0, checksum.getValue());
    }

}
