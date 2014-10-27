package de.greenrobot.common.checksum;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

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

}
