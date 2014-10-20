package de.greenrobot.common;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.zip.Adler32;
import java.util.zip.CRC32;

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
        checksum.update((byte[]) null);
        checksum.update((int[]) null);
        checksum.update((short[]) null);
        checksum.update((long[]) null);
        checksum.updateUtf8(null);
        Assert.assertEquals(before, checksum.getValue());
    }

    @Test
    public void hashCollider() {
        Random random = new Random(42);
        byte[] bytes = new byte[1024];

        AdlerCrcCombinedChecksum combined = new AdlerCrcCombinedChecksum();
        CRC32 crc32 = new CRC32();
        Adler32 adler32 = new Adler32();

        Set<Long> combinedValues = new HashSet<>();
        Set<Long> crc32Values = new HashSet<>();
        Set<Long> adler32Values = new HashSet<>();

        int combinedCollisions = 0;
        int crc32Collisions = 0;
        int adler32Collisions = 0;

        for (int i = 0; i < 1000000; i++) {
            random.nextBytes(bytes);
            combined.reset();
            crc32.reset();
            adler32.reset();

            crc32.update(bytes);
            if (!crc32Values.add(crc32.getValue())) {
                crc32Collisions++;
            }

            adler32.update(bytes);
            if (!adler32Values.add(adler32.getValue())) {
                adler32Collisions++;
            }

            combined.update(bytes);
            if (!combinedValues.add(combined.getValue())) {
                combinedCollisions++;
            }
        }

        System.out.println("Adler32/CRC32/Combined collisions: " +
                adler32Collisions + "/" + crc32Collisions + "/" + combinedCollisions);
    }
}
