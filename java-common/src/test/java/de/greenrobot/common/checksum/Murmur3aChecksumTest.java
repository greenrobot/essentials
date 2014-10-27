package de.greenrobot.common.checksum;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Random;
import java.util.zip.Adler32;

public class Murmur3aChecksumTest extends AbstractChecksumTest {
    public Murmur3aChecksumTest() {
        super(new Murmur3aChecksum());
    }

    @Test
    public void testExpectedHashVariableLength() {
        byte[] bytes = new byte[512];
        new Random(23).nextBytes(bytes);
        for (int i = 0; i <= bytes.length; i++) {
            int expected = Murmur3aHash.murmurhash3_x86_32(bytes, 0, i, 0);
            checksum.reset();
            checksum.update(bytes, 0, i);
            int value = (int) checksum.getValue();
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testExpectedHashVariableOffset() {
        byte[] bytes = new byte[512];
        new Random(31).nextBytes(bytes);
        for (int i = 0; i <= bytes.length; i++) {
            int expected = Murmur3aHash.murmurhash3_x86_32(bytes, i, bytes.length - i, 0);
            checksum.reset();
            checksum.update(bytes, i, bytes.length - i);
            int value = (int) checksum.getValue();
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testSeed() {
        Random random = new Random(511);
        for (int i = 0; i <= 512; i++) {
            int seed = random.nextInt();
            int expected = Murmur3aHash.murmurhash3_x86_32(INPUT4, 0, INPUT4.length, seed);
            checksum = new Murmur3aChecksum(seed);
            checksum.update(INPUT4, 0, INPUT4.length);
            int value = (int) checksum.getValue();
            Assert.assertEquals("i=" + i, expected, value);
        }
    }

    @Test
    public void testUpdateIntAligned() throws Exception {
        int input = Integer.MIN_VALUE + 123456789;
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(input);
        byte[] bytes = byteBuffer.array();

        Murmur3aChecksum murmur3aChecksum = (Murmur3aChecksum) checksum;
        murmur3aChecksum.updateInt(input);
        long value1 = murmur3aChecksum.getValue();

        murmur3aChecksum.reset();
        murmur3aChecksum.update(bytes, 0, bytes.length);
        long value2 = murmur3aChecksum.getValue();
        Assert.assertEquals(value2, value1);
    }

    @Test
    public void testUpdateIntMixed() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(12);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put((byte) 42);
        byteBuffer.putInt(1234567890);
        byteBuffer.put((byte) 13);
        byteBuffer.put((byte) 23);
        byteBuffer.put((byte) 33);
        byteBuffer.putInt(1000000031);
        byte[] bytes = byteBuffer.array();

        Murmur3aChecksum murmur3aChecksum = (Murmur3aChecksum) checksum;
        murmur3aChecksum.update(42);
        murmur3aChecksum.updateInt(1234567890);
        murmur3aChecksum.update(13);
        murmur3aChecksum.update(23);
        murmur3aChecksum.update(33);
        murmur3aChecksum.updateInt(1000000031);
        long value1 = murmur3aChecksum.getValue();

        murmur3aChecksum.reset();
        murmur3aChecksum.update(bytes, 0, bytes.length);
        long value2 = murmur3aChecksum.getValue();
        Assert.assertEquals(value2, value1);
    }

}
