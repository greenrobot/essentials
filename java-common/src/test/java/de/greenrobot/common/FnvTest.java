package de.greenrobot.common;

import org.junit.Assert;
import org.junit.Test;

public class FnvTest {
    private final static byte[] INPUT32_ZERO1 = {(byte) 0xcc, 0x24, 0x31, (byte) 0xc4};
    private final static byte[] INPUT32_ZERO2 = {(byte) 0xe0, 0x4d, (byte) 0x9f, (byte) 0xcb};
    private final static byte[] INPUT64_ZERO = {(byte) 0xd5, 0x6b, (byte) 0xb9, 0x53, 0x42, (byte) 0x87, 0x08, 0x36};

    @Test
    public void testFnv32UpdateZeroHash() {
        FNV32 checksum = new FNV32();
        for (int b : INPUT32_ZERO1) {
            checksum.update(b);
        }
        Assert.assertEquals(0, checksum.getValue());

        checksum.reset();
        for (int b : INPUT32_ZERO2) {
            checksum.update(b);
        }
        Assert.assertEquals(0, checksum.getValue());
    }

    @Test
    public void testFnv32UpdateBytesZeroHash() {
        FNV32 checksum = new FNV32();
        checksum.update(INPUT32_ZERO1, 0, INPUT32_ZERO1.length);
        Assert.assertEquals(0, checksum.getValue());

        checksum.reset();
        checksum.update(INPUT32_ZERO2, 0, INPUT32_ZERO1.length);
        Assert.assertEquals(0, checksum.getValue());
    }

    @Test
    public void testFnv64UpdateZeroHash() {
        FNV64 checksum = new FNV64();
        for (int b : INPUT64_ZERO) {
            checksum.update(b);
        }
        Assert.assertEquals(0, checksum.getValue());
    }

    @Test
    public void testFnv64UpdateBytesZeroHash() {
        FNV64 checksum = new FNV64();
        checksum.update(INPUT64_ZERO, 0, INPUT64_ZERO.length);
        Assert.assertEquals(0, checksum.getValue());
    }

}
