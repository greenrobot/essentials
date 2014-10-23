package de.greenrobot.common.checksum;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.zip.Adler32;
import java.util.zip.CRC32;

public class DataChecksumTest {

    @Test
    public void testUpdateInt() throws Exception {
        int input = Integer.MIN_VALUE + 123456789;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new DataOutputStream(byteArrayOutputStream).writeInt(input);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        DataChecksum checksum = new DataChecksum(new Adler32());
        checksum.updateInt(input);
        long value1 = checksum.getValue();

        DataChecksum checksum2 = new DataChecksum(new Adler32());
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

        DataChecksum checksum = new DataChecksum(new Adler32());
        checksum.updateShort(input);
        long value1 = checksum.getValue();

        DataChecksum checksum2 = new DataChecksum(new Adler32());
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

        DataChecksum checksum = new DataChecksum(new Adler32());
        checksum.updateLong(input);
        long value1 = checksum.getValue();

        DataChecksum checksum2 = new DataChecksum(new Adler32());
        checksum2.update(bytes, 0, bytes.length);
        long value2 = checksum2.getValue();

        Assert.assertEquals(value2, value1);
    }

    @Test
    public void testNullValues() throws Exception {
        DataChecksum checksum = new DataChecksum(new Adler32());
        long before = checksum.getValue();
        checksum.update((byte[]) null);
        checksum.update((int[]) null);
        checksum.update((short[]) null);
        checksum.update((long[]) null);
        checksum.updateUtf8((String) null);
        checksum.updateUtf8((String[]) null);
        Assert.assertEquals(before, checksum.getValue());
    }

}
