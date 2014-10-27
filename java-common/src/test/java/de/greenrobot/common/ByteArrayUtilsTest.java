package de.greenrobot.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class ByteArrayUtilsTest {
    private byte[] bytes;
    private ByteBuffer byteBufferLE;
    private ByteBuffer byteBufferBE;

    @Before
    public void setUp() {
        bytes = new byte[102400];
        new Random(42).nextBytes(bytes);

        byteBufferLE = ByteBuffer.wrap(bytes);
        byteBufferLE.order(ByteOrder.LITTLE_ENDIAN);
        byteBufferBE = ByteBuffer.wrap(bytes);
    }

    @Test
    public void testGetIntLE() {
        for (int i = 0; i < bytes.length - 3; i++) {
            int expected = byteBufferLE.getInt(i);
            int value = ByteArrayUtils.getIntLE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetIntLEPlainJava() {
        for (int i = 0; i < bytes.length - 3; i++) {
            int expected = byteBufferLE.getInt(i);
            int value = ByteArrayUtils.getIntLEPlainJava(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetLongLE() {
        for (int i = 0; i < bytes.length - 7; i++) {
            long expected = byteBufferLE.getLong(i);
            long value = ByteArrayUtils.getLongLE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetLongLEPlainJava() {
        for (int i = 0; i < bytes.length - 7; i++) {
            long expected = byteBufferLE.getLong(i);
            long value = ByteArrayUtils.getLongLEPlainJava(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetIntBE() {
        for (int i = 0; i < bytes.length - 3; i++) {
            int expected = byteBufferBE.getInt(i);
            int value = ByteArrayUtils.getIntBE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetIntBEPlainJava() {
        for (int i = 0; i < bytes.length - 3; i++) {
            int expected = byteBufferBE.getInt(i);
            int value = ByteArrayUtils.getIntBEPlainJava(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetLongBE() {
        for (int i = 0; i < bytes.length - 7; i++) {
            long expected = byteBufferBE.getLong(i);
            long value = ByteArrayUtils.getLongBE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetLongBEPlainJava() {
        for (int i = 0; i < bytes.length - 7; i++) {
            long expected = byteBufferBE.getLong(i);
            long value = ByteArrayUtils.getLongBEPlainJava(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }
}
