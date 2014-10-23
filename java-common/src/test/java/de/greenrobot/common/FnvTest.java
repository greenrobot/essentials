package de.greenrobot.common;

import org.junit.Assert;
import org.junit.Test;

public class FnvTest {
    private final static byte[] INPUT_ZERO1 = {(byte) 0xcc, 0x24, 0x31, (byte) 0xc4};
    private final static byte[] INPUT_ZERO2 = {(byte) 0xe0, 0x4d, (byte) 0x9f, (byte) 0xcb};

    @Test
    public void testFnv32UpdateZeroHash() {
        FNV32 checksum = new FNV32();
        for (int b : INPUT_ZERO1) {
            checksum.update(b);
        }
        Assert.assertEquals(0, checksum.getValue());

        checksum.reset();
        for (int b : INPUT_ZERO2) {
            checksum.update(b);
        }
        Assert.assertEquals(0, checksum.getValue());
    }

    @Test
    public void testFnv32UpdateBytesZeroHash() {
        FNV32 checksum = new FNV32();
        checksum.update(INPUT_ZERO1, 0, INPUT_ZERO1.length);
        Assert.assertEquals(0, checksum.getValue());

        checksum.reset();
        checksum.update(INPUT_ZERO2, 0, INPUT_ZERO1.length);
        Assert.assertEquals(0, checksum.getValue());
    }

}
