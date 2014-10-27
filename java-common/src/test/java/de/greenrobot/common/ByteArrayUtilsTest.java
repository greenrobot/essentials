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

    @Before
    public void setUp() {
        bytes = new byte[102400];
        new Random(42).nextBytes(bytes);

        byteBufferLE = ByteBuffer.wrap(bytes);
        byteBufferLE.order(ByteOrder.LITTLE_ENDIAN);
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
}
