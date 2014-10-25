package de.greenrobot.common.checksum;

import org.junit.Assert;
import org.junit.Test;

public class FNVJTest {
    private final static byte[] INPUT4 = {(byte) 0xcc, 0x24, 0x31, (byte) 0xc4};
    private final static byte[] INPUT15 = {(byte) 0xe0, 0x4d, (byte) 0x9f, (byte) 0xcb, (byte) 0xd5, 0x6b,
            (byte) 0xb9, 0x53, 0x42, (byte) 0x87, 0x08, 0x36, 0x77, 0x23, 0x01};

    @Test
    public void testBasics() {
        FNVJ32 checksum = new FNVJ32();
        long initialHash = checksum.getValue();

        for (int b : INPUT4) {
            checksum.update(b);
            Assert.assertNotEquals(initialHash, checksum.getValue());
        }
        long hash = checksum.getValue();

        checksum.reset();
        Assert.assertEquals(initialHash, checksum.getValue());

        checksum.update(INPUT4, 0, INPUT4.length);
        Assert.assertEquals(hash, checksum.getValue());
    }

    @Test
    public void testRestUnaligned() {
        FNVJ32 checksum = new FNVJ32();
        checksum.update(42);
        long hash = checksum.getValue();
        checksum.reset();
        checksum.update(42);
        Assert.assertEquals(hash, checksum.getValue());
    }


    @Test
    public void testMixedUnaligned() {
        FNVJ32 checksum = new FNVJ32();
        checksum.update(INPUT15, 0, INPUT15.length);
        long hash = checksum.getValue();

        checksum.reset();
        checksum.update(INPUT15, 0, 2);
        checksum.update(INPUT15[2]);
        checksum.update(INPUT15, 3, 11);
        checksum.update(INPUT15[14]);
        Assert.assertEquals(hash, checksum.getValue());
    }

}
