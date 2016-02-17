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

package de.greenrobot.common.io;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
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
    public void testPutPartial() {
        int capacity = 16;
        CircularByteBuffer buffer = new CircularByteBuffer(capacity);
        byte[] bytes = createBytes(10);
        assertEquals(10, buffer.put(bytes));
        assertEquals(6, buffer.put(bytes));
        assertEquals(0, buffer.put(bytes));

        byte[] bytesGet = new byte[16];
        assertEquals(16, buffer.get(bytesGet));
        assertTrue(Arrays.equals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6}, bytesGet));
    }

    /** All possible start positions with all possible lengths */
    @Test
    public void testPutAndGet() {
        int capacity = 16;
        for (int startPosition = 0; startPosition <= capacity; startPosition++) {
            for (int length = 1; length <= capacity; length++) {
                CircularByteBuffer buffer = new CircularByteBuffer(capacity);
                byte[] prepBytes = new byte[startPosition];
                assertEquals(startPosition, buffer.put(prepBytes));
                assertEquals(startPosition, buffer.get(prepBytes));

                byte[] bytes = createBytes(length);
                assertEquals(length, buffer.put(bytes));
                assertEquals(length, buffer.available());
                byte[] bytesGet = new byte[length];
                assertEquals(length, buffer.get(bytesGet));
                assertEquals(0, buffer.available());
                assertTrue(Arrays.equals(bytes, bytesGet));
            }
        }
    }

    private byte[] createBytes(int len) {
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (i + 1);
        }
        return bytes;
    }

}
