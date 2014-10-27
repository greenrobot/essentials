package de.greenrobot.common.checksum;

import org.junit.Assert;
import org.junit.Test;

public class FNV32Test extends AbstractChecksumTest {
    private final static byte[] INPUT32_ZERO1 = {(byte) 0xcc, 0x24, 0x31, (byte) 0xc4};
    private final static byte[] INPUT32_ZERO2 = {(byte) 0xe0, 0x4d, (byte) 0x9f, (byte) 0xcb};

    public FNV32Test() {
        super(new FNV32());
    }

    @Test
    public void testFnv32UpdateZeroHash() {
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
        checksum.update(INPUT32_ZERO1, 0, INPUT32_ZERO1.length);
        Assert.assertEquals(0, checksum.getValue());

        checksum.reset();
        checksum.update(INPUT32_ZERO2, 0, INPUT32_ZERO1.length);
        Assert.assertEquals(0, checksum.getValue());
    }

}
