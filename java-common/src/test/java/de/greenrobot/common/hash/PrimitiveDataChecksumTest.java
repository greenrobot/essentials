package de.greenrobot.common.hash;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.zip.Adler32;

@RunWith(Parameterized.class)
public class PrimitiveDataChecksumTest extends AbstractAllChecksumTest {

    private PrimitiveDataChecksum primitiveDataChecksum;

    @Before
    public void setUp() {
        primitiveDataChecksum = new PrimitiveDataChecksum(checksum);
    }

    @Test
    public void testUpdateInt() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new DataOutputStream(out).writeInt(1234567890);
        long expected = getHashAndReset(out);
        primitiveDataChecksum.updateInt(1234567890);
        Assert.assertEquals(expected, primitiveDataChecksum.getValue());
    }

    @Test
    public void testUpdateBoolean() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new DataOutputStream(out).writeBoolean(true);
        long expected = getHashAndReset(out);
        primitiveDataChecksum.updateBoolean(true);
        Assert.assertEquals(expected, primitiveDataChecksum.getValue());
    }

    @Test
    public void testUpdateShort() throws Exception {
        short input = Short.MIN_VALUE + 12345;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new DataOutputStream(out).writeShort(input);
        long expected = getHashAndReset(out);
        primitiveDataChecksum.updateShort(input);
        Assert.assertEquals(expected, primitiveDataChecksum.getValue());
    }

    @Test
    public void testUpdateLong() throws Exception {
        long input = Long.MIN_VALUE + 123456789;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new DataOutputStream(out).writeLong(input);
        long expected = getHashAndReset(out);
        primitiveDataChecksum.updateLong(input);
        Assert.assertEquals(expected, primitiveDataChecksum.getValue());
    }

    @Test
    public void testUpdateFloat() throws Exception {
        float input = (float) -Math.PI;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new DataOutputStream(out).writeFloat(input);
        long expected = getHashAndReset(out);
        primitiveDataChecksum.updateFloat(input);
        Assert.assertEquals(expected, primitiveDataChecksum.getValue());
    }

    @Test
    public void testUpdateDouble() throws Exception {
        double input = -Math.PI;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new DataOutputStream(out).writeDouble(input);
        long expected = getHashAndReset(out);
        primitiveDataChecksum.updateDouble(input);
        Assert.assertEquals(expected, primitiveDataChecksum.getValue());
    }

    @Test
    public void testNullValues() throws Exception {
        PrimitiveDataChecksum checksum = new PrimitiveDataChecksum(new Adler32());
        long before = checksum.getValue();
        checksum.update((byte[]) null);
        checksum.update((int[]) null);
        checksum.update((short[]) null);
        checksum.update((long[]) null);
        checksum.update((float[]) null);
        checksum.update((double[]) null);
        checksum.updateUtf8((String) null);
        checksum.updateUtf8((String[]) null);
        Assert.assertEquals(before, checksum.getValue());
    }

    private long getHashAndReset(ByteArrayOutputStream out) {
        primitiveDataChecksum.reset();
        byte[] bytes = out.toByteArray();
        primitiveDataChecksum.update(bytes, 0, bytes.length);
        long value = primitiveDataChecksum.getValue();
        primitiveDataChecksum.reset();
        return value;
    }


}
