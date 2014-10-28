package de.greenrobot.common.checksum;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class Murmur3aChecksumTest extends AbstractChecksumTest {
    private final Murmur3aChecksum murmur3aChecksum;

    public Murmur3aChecksumTest() {
        super(new Murmur3aChecksum());
        murmur3aChecksum = (Murmur3aChecksum) checksum;
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
    // Meta test
    public void testAlignmentTest() throws Exception {
        for (int i = 0; i < 16; i++) {
            ByteBuffer byteBuffer = prepareByteBufferLE(i, 0);
            prepareMurmur3aChecksum(i);
            assertEqualHash(byteBuffer, murmur3aChecksum);
        }
    }


    @Test
    public void testUpdateShortAlignment() throws Exception {
        for (int i = 0; i < 16; i++) {
            ByteBuffer byteBuffer = prepareByteBufferLE(i, 3);
            byteBuffer.putShort((short) 12345);
            byteBuffer.put((byte) 23);

            prepareMurmur3aChecksum(i);
            murmur3aChecksum.updateShort((short) 12345);
            murmur3aChecksum.update((byte) 23); // One more byte to check state is still OK
            assertEqualHash(byteBuffer, murmur3aChecksum);
        }
    }

    @Test
    public void testUpdateIntAlignment() throws Exception {
        for (int i = 0; i < 16; i++) {
            ByteBuffer byteBuffer = prepareByteBufferLE(i, 5);
            byteBuffer.putInt(1234567890);
            byteBuffer.put((byte) 23);

            prepareMurmur3aChecksum(i);
            murmur3aChecksum.updateInt(1234567890);
            murmur3aChecksum.update((byte) 23); // One more byte to check state is still OK
            assertEqualHash(byteBuffer, murmur3aChecksum);
        }
    }

    @Test
    public void testUpdateLongAlignment() throws Exception {
        for (int i = 0; i < 16; i++) {
            ByteBuffer byteBuffer = prepareByteBufferLE(i, 9);
            byteBuffer.putLong(1234567890123456789L);
            byteBuffer.put((byte) 23);

            prepareMurmur3aChecksum(i);
            murmur3aChecksum.updateLong(1234567890123456789L);
            murmur3aChecksum.update((byte) 23); // One more byte to check state is still OK
            assertEqualHash(byteBuffer, murmur3aChecksum);
        }
    }

    @Test
    public void testUpdateIntMixed() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(13);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put((byte) 42);
        byteBuffer.putInt(1234567890);
        byteBuffer.put((byte) 13);
        byteBuffer.put((byte) 23);
        byteBuffer.put((byte) 33);
        byteBuffer.putInt(1000000031);
        byteBuffer.put((byte) 99);
        byte[] bytes = byteBuffer.array();

        Murmur3aChecksum murmur3aChecksum = (Murmur3aChecksum) checksum;
        murmur3aChecksum.update(42);
        murmur3aChecksum.updateInt(1234567890);
        murmur3aChecksum.update(13);
        murmur3aChecksum.update(23);
        murmur3aChecksum.update(33);
        murmur3aChecksum.updateInt(1000000031);
        murmur3aChecksum.update(99);
        long value1 = murmur3aChecksum.getValue();

        murmur3aChecksum.reset();
        murmur3aChecksum.update(bytes, 0, bytes.length);
        long value2 = murmur3aChecksum.getValue();
        Assert.assertEquals(value2, value1);
    }

    @Test
    public void testUpdateLongMixed() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(17);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put((byte) 42);
        byteBuffer.putInt(1234567890);
        byteBuffer.put((byte) 13);
        byteBuffer.put((byte) 23);
        byteBuffer.put((byte) 33);
        byteBuffer.putLong(10000000317777L);
        byteBuffer.put((byte) 99);
        byte[] bytes = byteBuffer.array();
        checksum.update(bytes, 0, bytes.length);
        long expected = checksum.getValue();

        Murmur3aChecksum murmur3aChecksum = (Murmur3aChecksum) checksum;
        murmur3aChecksum.reset();
        murmur3aChecksum.update(42);
        murmur3aChecksum.updateInt(1234567890);
        murmur3aChecksum.update(13);
        murmur3aChecksum.update(23);
        murmur3aChecksum.update(33);
        murmur3aChecksum.updateLong(10000000317777L);
        murmur3aChecksum.update(99);
        long value1 = murmur3aChecksum.getValue();

        Assert.assertEquals(expected, value1);
    }

    private void prepareMurmur3aChecksum(int prefixLength) {
        murmur3aChecksum.reset();
        for (int j = 0; j < prefixLength; j++) {
            murmur3aChecksum.update((byte) (0x77 + j));
        }
    }

    private ByteBuffer prepareByteBufferLE(int prefixLength, int additionalLength) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(prefixLength + additionalLength);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int j = 0; j < prefixLength; j++) {
            byteBuffer.put((byte) (0x77 + j));
        }
        return byteBuffer;
    }

    private long getHash(ByteBuffer byteBuffer) {
        byte[] bytes = byteBuffer.array();
        checksum.reset();
        checksum.update(bytes, 0, bytes.length);
        long value = checksum.getValue();
        checksum.reset();
        return value;
    }

    private void assertEqualHash(ByteBuffer byteBuffer, Murmur3aChecksum murmur3aChecksum) {
        long value = murmur3aChecksum.getValue();
        long expected = getHash(byteBuffer);
        Assert.assertEquals("BB capacity: " + byteBuffer.capacity(), expected, value);

        // Sanity check
        if (byteBuffer.capacity() > 0) {
            Assert.assertNotEquals(0, value);
        }
    }


}
