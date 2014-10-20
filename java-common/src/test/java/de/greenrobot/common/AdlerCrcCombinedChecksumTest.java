package de.greenrobot.common;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class AdlerCrcCombinedChecksumTest {
    @Test
    public void testBasics() throws Exception {
        AdlerCrcCombinedChecksum checksum = new AdlerCrcCombinedChecksum();
        long emptyValue = checksum.getValue();
        for (int i = 0; i < 256; i++) {
            checksum.update(i);
            long value = checksum.getValue();

            long crc32 = value & 0xffffffff;
            long adler32 = (value >>> 32) & 0xffffffff;

            Assert.assertNotEquals(crc32, adler32);
            Assert.assertNotEquals(0, adler32);
            Assert.assertNotEquals(0, crc32);
        }

        checksum.reset();
        Assert.assertEquals(emptyValue, checksum.getValue());
    }

    @Test
    public void testUpdateInt() throws Exception {
        int input = Integer.MIN_VALUE + 123456789;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new DataOutputStream(byteArrayOutputStream).writeInt(input);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        AdlerCrcCombinedChecksum checksum = new AdlerCrcCombinedChecksum();
        checksum.updateInt(input);
        long value1 = checksum.getValue();

        AdlerCrcCombinedChecksum checksum2 = new AdlerCrcCombinedChecksum();
        checksum2.update(bytes, 0, bytes.length);
        long value2 = checksum2.getValue();
        Assert.assertEquals(value2, value1);
    }

    @Test
    public void testUpdateShort() throws Exception {
        short input = Short.MIN_VALUE + 12345;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new DataOutputStream(byteArrayOutputStream).writeShort(input);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        AdlerCrcCombinedChecksum checksum = new AdlerCrcCombinedChecksum();
        checksum.updateShort(input);
        long value1 = checksum.getValue();

        AdlerCrcCombinedChecksum checksum2 = new AdlerCrcCombinedChecksum();
        checksum2.update(bytes, 0, bytes.length);
        long value2 = checksum2.getValue();

        Assert.assertEquals(value2, value1);
    }

    @Test
    public void testUpdateLong() throws Exception {
        long input = Long.MIN_VALUE + 123456789;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new DataOutputStream(byteArrayOutputStream).writeLong(input);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        AdlerCrcCombinedChecksum checksum = new AdlerCrcCombinedChecksum();
        checksum.updateLong(input);
        long value1 = checksum.getValue();

        AdlerCrcCombinedChecksum checksum2 = new AdlerCrcCombinedChecksum();
        checksum2.update(bytes, 0, bytes.length);
        long value2 = checksum2.getValue();

        Assert.assertEquals(value2, value1);
    }

    @Test
    public void testNullValues() throws Exception {
        AdlerCrcCombinedChecksum checksum = new AdlerCrcCombinedChecksum();
        long before = checksum.getValue();
        checksum.update((int[]) null);
        checksum.update((short[]) null);
        checksum.update((long[]) null);
        checksum.updateUtf8(null);
        Assert.assertEquals(before, checksum.getValue());
    }
}
