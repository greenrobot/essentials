package de.greenrobot.common.checksum;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class Murmur3fChecksumTest extends AbstractChecksumTest {
    private final Murmur3fChecksum murmur3fChecksum;

    public Murmur3fChecksumTest() {
        super(new Murmur3fChecksum());
        murmur3fChecksum = (Murmur3fChecksum) checksum;
    }


    @Test
    public void testExpectedHash() {
        // 0 MurmurHash3_x64_128 hash lo: cfa0f7ddd84c76bc hash hi: 589623161cf526f1
        // I4 MurmurHash3_x64_128 hash lo: 14885fe730885297 hash hi: 1e5a73f96044885e
        // I16 MurmurHash3_x64_128 hash lo: edb199d42d778ebb hash hi: c6dec4069552440b
        super.testExpectedHash(0xcfa0f7ddd84c76bcL, 0x14885fe730885297L, 0xedb199d42d778ebbL);
    }

    @Test
    public void testCompareWithGuava() {
        byte[] bytes = new byte[1024];
        new Random(42).nextBytes(bytes);

        for (int i = 0; i <= bytes.length; i++) {
            HashCode hashCode = Hashing.murmur3_128().hashBytes(bytes, 0, i);
            long expected = hashCode.asLong();// 64 bit is enough

            checksum.reset();
            checksum.update(bytes, 0, i);
            Assert.assertEquals("Iteration " + i, expected, checksum.getValue());
        }

        for (int i = 0; i < bytes.length; i++) {
            HashCode hashCode = Hashing.murmur3_128().hashBytes(bytes, i, bytes.length - i);
            long expected = hashCode.asLong();// 64 bit is enough

            checksum.reset();
            checksum.update(bytes, i, bytes.length - i);
            Assert.assertEquals("Iteration " + i, expected, checksum.getValue());
        }
    }


    @Test
    public void testGetValueBytesAgainstGuava() {
        byte[] expected = Hashing.murmur3_128().hashBytes(INPUT4).asBytes();
        murmur3fChecksum.update(INPUT4, 0, INPUT4.length);
        byte[] bytes = murmur3fChecksum.getValueBytesLittleEndian();
        Assert.assertArrayEquals(expected, bytes);
    }

    @Test
    public void testGetValueBytesEndian() {
        murmur3fChecksum.update(INPUT4, 0, INPUT4.length);
        byte[] bytesLE = murmur3fChecksum.getValueBytesLittleEndian();
        byte[] bytesBE = murmur3fChecksum.getValueBytesBigEndian();
        for (int i = 0; i < bytesLE.length; i++) {
            Assert.assertEquals(bytesLE[i], bytesBE[bytesBE.length - 1 - i]);
        }
    }

    @Test
    public void testGetBigValue() {
        murmur3fChecksum.update(42);
        String expected = Long.toHexString(murmur3fChecksum.getValue2()) +
                Long.toHexString(murmur3fChecksum.getValue());
        Assert.assertEquals(32, expected.length()); // For this particular hash OK
        String big = murmur3fChecksum.getValueBigInteger().toString(16);
        Assert.assertEquals(expected, big);
    }

    @Test
    public void testGetValueHexString() {
        murmur3fChecksum.update(42);
        String expected = Long.toHexString(murmur3fChecksum.getValue2()) +
                Long.toHexString(murmur3fChecksum.getValue());
        Assert.assertEquals(32, expected.length()); // For this particular hash OK
        Assert.assertEquals(expected, murmur3fChecksum.getValueHexString());
    }

    @Test
    public void testGetValueHexStringPadded() {
        while (true) {
            murmur3fChecksum.update(42);
            String nonPadded = Long.toHexString(murmur3fChecksum.getValue2());
            int delta = 16 - nonPadded.length();
            if (delta > 0) {
                String padded = murmur3fChecksum.getValueHexString();
                for (int i = 0; i < delta; i++) {
                    Assert.assertEquals('0', padded.charAt(i));
                }
                Assert.assertNotEquals('0', padded.charAt(delta));
                if (delta > 2) {
                    break;
                }
            }

        }
    }

}
