package de.greenrobot.common.checksum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.zip.Checksum;

public abstract class AbstractChecksumTest {
    protected final static byte[] INPUT4 = {(byte) 0xcc, 0x24, 0x31, (byte) 0xc4};
    protected final static byte[] INPUT16 = {(byte) 0xe0, 0x4d, (byte) 0x9f, (byte) 0xcb, (byte) 0xd5, 0x6b,
            (byte) 0xb9, 0x53, 0x42, (byte) 0x87, 0x08, 0x36, 0x77, 0x23, 0x01, 0};

    protected Checksum checksum;

    protected AbstractChecksumTest(Checksum checksum) {
        this.checksum = checksum;
    }

    @Before
    public void setUp() {
        checksum.reset();
    }

    @Test
    public void testBasics() {
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
    public void testGetValueStable() {
        checksum.update(INPUT16, 0, INPUT16.length);
        long hash = checksum.getValue();
        // Calling checksum.getValue() twice should not change hash
        Assert.assertEquals(hash, checksum.getValue());
    }

    public void testExpectedHash(long expectedFor0, long expectedForInput4, long expectedForInput16) {
        checksum.update(0);
        checksum.update(0);
        checksum.update(0);
        checksum.update(0);
        Assert.assertEquals(expectedFor0, checksum.getValue());

        checksum.reset();
        checksum.update(INPUT4, 0, INPUT4.length);
        Assert.assertEquals(expectedForInput4, checksum.getValue());

        checksum.reset();
        checksum.update(INPUT16, 0, INPUT16.length);
        Assert.assertEquals(expectedForInput16, checksum.getValue());
    }

    @Test
    public void testRestUnaligned() {
        checksum.update(42);
        long hash = checksum.getValue();
        checksum.reset();
        checksum.update(42);
        Assert.assertEquals(hash, checksum.getValue());
    }

    @Test
    public void testMixedUnaligned() {
        checksum.update(INPUT16, 0, INPUT16.length);
        long hash = checksum.getValue();

        checksum.reset();
        checksum.update(INPUT16, 0, 2);
        checksum.update(INPUT16[2]);
        checksum.update(INPUT16, 3, 11);
        checksum.update(INPUT16[14]);
        checksum.update(INPUT16[15]);
        Assert.assertEquals(hash, checksum.getValue());
    }

    @Test
    public void testTrailingZero() {
        long lastHash = checksum.getValue();
        Assert.assertEquals(0, INPUT16[INPUT16.length - 1]);
        for (int b : INPUT16) {
            checksum.update(b);
            long hash = checksum.getValue();
            Assert.assertNotEquals(lastHash, hash);
            lastHash = hash;
        }
    }

}
