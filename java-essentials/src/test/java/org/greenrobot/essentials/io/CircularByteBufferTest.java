/*
 * Copyright (C) 2014-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.essentials.io;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CircularByteBufferTest {

    @Test
    public void testStats() {
        int capacity = 16;
        CircularByteBuffer buffer = new CircularByteBuffer(capacity);
        assertEquals(capacity, buffer.capacity());
        assertEquals(capacity, buffer.free());
        assertEquals(0, buffer.available());

        buffer.put(new byte[4]);
        assertEquals(4, buffer.available());
        assertEquals(12, buffer.free());
    }

    @Test
    public void testClear() {
        int capacity = 16;
        CircularByteBuffer buffer = new CircularByteBuffer(capacity);
        buffer.put(new byte[4]);
        buffer.clear();
        assertEquals(0, buffer.available());
        assertEquals(capacity, buffer.free());

        // Test room available
        assertEquals(16, buffer.put(new byte[16]));
    }

    @Test
    public void testOffsetAndLen() {
        int capacity = 16;
        CircularByteBuffer buffer = new CircularByteBuffer(capacity);
        byte[] bytes = createBytes(100);
        assertEquals(10, buffer.put(bytes, 19, 10));
        assertEquals(1, buffer.put(bytes, 49, 1));
        assertEquals(5, buffer.put(bytes, 59, 50));
        assertEquals(16, buffer.available());

        getAndAssertEqualContent(buffer, new byte[]{20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 50, 60, 61, 62, 63, 64});
    }

    @Test
    public void testPutPartial() {
        int capacity = 16;
        CircularByteBuffer buffer = new CircularByteBuffer(capacity);
        byte[] bytes = createBytes(10);
        assertEquals(10, buffer.put(bytes));
        assertEquals(6, buffer.put(bytes));
        assertEquals(0, buffer.put(bytes));

        getAndAssertEqualContent(buffer, new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6});
    }

    @Test
    public void testGetPartial() {
        int capacity = 16;
        CircularByteBuffer buffer = new CircularByteBuffer(capacity);
        byte[] bytes = createBytes(10);
        buffer.put(bytes);

        byte[] bytesGet = new byte[100];
        assertEquals(10, buffer.get(bytesGet));
        assertEquals(0, buffer.get(bytesGet));
        assertEquals(0, buffer.get(bytesGet, 50, 10));

        for (int i = 0; i < bytesGet.length; i++) {
            assertEquals(i < bytes.length ? bytes[i] : 0, bytesGet[i]);
        }
    }

    /** All possible start positions with all possible lengths */
    @Test
    public void testPutAndGet() {
        int capacity = 16;
        for (int startPosition = 0; startPosition <= capacity; startPosition++) {
            for (int length = 1; length <= capacity; length++) {
                for (int putLength1 = 0; putLength1 <= length; putLength1++) {
                    for (int getLength1 = 0; getLength1 <= length; getLength1++) {
                        CircularByteBuffer buffer = new CircularByteBuffer(capacity);
                        byte[] prepBytes = new byte[startPosition];
                        assertEquals(startPosition, buffer.put(prepBytes));
                        assertEquals(startPosition, buffer.get(prepBytes));

                        byte[] bytes = createBytes(length);
                        assertEquals(putLength1, buffer.put(bytes, 0, putLength1));
                        assertEquals(putLength1, buffer.available());
                        int putLength2 = length - putLength1;
                        assertEquals(putLength2, buffer.put(bytes, putLength1, putLength2));

                        assertEquals(length, buffer.available());

                        byte[] bytesGet = new byte[length];
                        assertEquals(getLength1, buffer.get(bytesGet, 0, getLength1));
                        int getLength2 = length - getLength1;
                        assertEquals(getLength2, buffer.available());
                        assertEquals(getLength2, buffer.get(bytesGet, getLength1, getLength2));

                        assertTrue(Arrays.equals(bytes, bytesGet));
                        assertEquals(0, buffer.available());
                    }
                }
            }
        }
    }

    @Test
    public void testSkipAndPeek() {
        int capacity = 17;
        CircularByteBuffer buffer = new CircularByteBuffer(capacity);
        byte[] bytes = createBytes(10);

        // Loop to test a couple of different internal start positions
        for (int i = 0; i < 10; i++) {
            buffer.put(bytes);

            assertEquals(2, buffer.skip(2));
            assertEquals(3, buffer.peek());

            assertEquals(8, buffer.skip(10));
            assertEquals(-1, buffer.peek());
        }
    }

    @Test
    public void testGetAndPutSingle() {
        int capacity = 17;
        CircularByteBuffer buffer = new CircularByteBuffer(capacity);
        int length = 10;
        byte[] bytes = createBytes(length);

        // Loop to test a couple of different internal start positions
        for (int i = 0; i < length; i++) {
            assertEquals(length, buffer.put(bytes));
            for (int j = 0; j < length; j++) {
                assertEquals(bytes[j], buffer.get());
            }
            assertEquals(-1, buffer.get());
            assertEquals(0, buffer.available());

            for (int j = 0; j < length; j++) {
                assertTrue(buffer.put(bytes[j]));
                assertEquals(j + 1, buffer.available());
            }
            getAndAssertEqualContent(buffer, bytes);
            assertEquals(0, buffer.available());
        }
    }

    @Test
    public void testGetAndPutSingleNoData() {
        CircularByteBuffer buffer = new CircularByteBuffer(1);
        assertTrue(buffer.put((byte) 42));
        assertFalse(buffer.put((byte) 42));

        assertEquals(42, buffer.get());
        assertEquals(-1, buffer.get());
    }


    private byte[] createBytes(int len) {
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (i + 1);
        }
        return bytes;
    }

    private void getAndAssertEqualContent(CircularByteBuffer buffer, byte[] bytes) {
        byte[] bytesGet = new byte[bytes.length];
        assertEquals(bytes.length, buffer.get(bytesGet));
        assertTrue(Arrays.equals(bytes, bytesGet));
    }

}
