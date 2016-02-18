/*
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.greenrobot.essentials;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class PrimitiveArrayUtilsTest {
    private byte[] bytes;
    private ByteBuffer byteBufferLE;
    private ByteBuffer byteBufferBE;

    PrimitiveArrayUtils primitiveArrayUtils = PrimitiveArrayUtils.getInstance();
    PrimitiveArrayUtils primitiveArrayUtilsSafe = PrimitiveArrayUtils.getInstanceSafe();

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
            int value = primitiveArrayUtils.getIntLE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetIntLEPlainJava() {
        for (int i = 0; i < bytes.length - 3; i++) {
            int expected = byteBufferLE.getInt(i);
            int value = primitiveArrayUtilsSafe.getIntLE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetLongLE() {
        for (int i = 0; i < bytes.length - 7; i++) {
            long expected = byteBufferLE.getLong(i);
            long value = primitiveArrayUtils.getLongLE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetLongLEPlainJava() {
        for (int i = 0; i < bytes.length - 7; i++) {
            long expected = byteBufferLE.getLong(i);
            long value = primitiveArrayUtilsSafe.getLongLE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetIntBE() {
        for (int i = 0; i < bytes.length - 3; i++) {
            int expected = byteBufferBE.getInt(i);
            int value = primitiveArrayUtils.getIntBE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetIntBEPlainJava() {
        for (int i = 0; i < bytes.length - 3; i++) {
            int expected = byteBufferBE.getInt(i);
            int value = primitiveArrayUtilsSafe.getIntBE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetLongBE() {
        for (int i = 0; i < bytes.length - 7; i++) {
            long expected = byteBufferBE.getLong(i);
            long value = primitiveArrayUtils.getLongBE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testGetLongBEPlainJava() {
        for (int i = 0; i < bytes.length - 7; i++) {
            long expected = byteBufferBE.getLong(i);
            long value = primitiveArrayUtilsSafe.getLongBE(bytes, i);
            Assert.assertEquals(expected, value);
        }
    }
}
