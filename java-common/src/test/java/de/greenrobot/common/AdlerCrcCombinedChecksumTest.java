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
        checksum.update(42);
        long value = checksum.getValue();

        long crc32 = value & 0xffffffff;
        long adler32 = (value >>> 32) & 0xffffffff;

        Assert.assertNotEquals(crc32, adler32);
        Assert.assertNotEquals(0, adler32);
        Assert.assertNotEquals(0, crc32);

        checksum.reset();
        Assert.assertEquals(emptyValue, checksum.getValue());
    }

    @Test
    public void testUpdateInt() throws Exception {
        int value = Integer.MIN_VALUE + 123456789;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new DataOutputStream(byteArrayOutputStream).writeInt(value);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        AdlerCrcCombinedChecksum checksum = new AdlerCrcCombinedChecksum();
        checksum.updateInt(value);
        long value1 = checksum.getValue();

        AdlerCrcCombinedChecksum checksum2 = new AdlerCrcCombinedChecksum();
        checksum2.update(bytes, 0, bytes.length);
        long value2 = checksum2.getValue();

        Assert.assertEquals(value2, value1);
    }

    @Test
    public void testUpdateLong() throws Exception {
        long value = Long.MIN_VALUE + 123456789;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new DataOutputStream(byteArrayOutputStream).writeLong(value);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        AdlerCrcCombinedChecksum checksum = new AdlerCrcCombinedChecksum();
        checksum.updateLong(value);
        long value1 = checksum.getValue();

        AdlerCrcCombinedChecksum checksum2 = new AdlerCrcCombinedChecksum();
        checksum2.update(bytes, 0, bytes.length);
        long value2 = checksum2.getValue();

        Assert.assertEquals(value2, value1);
    }

}
