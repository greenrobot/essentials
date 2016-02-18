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

package org.greenrobot.essentials.hash;

import org.greenrobot.essentials.hash.otherhashes.MurmurHash3Yonik;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class Murmur3ATest extends AbstractChecksumTest {
    private final Murmur3A murmur3a;

    public Murmur3ATest() {
        super(new Murmur3A());
        murmur3a = (Murmur3A) checksum;
    }

    @Test
    public void testExpectedHashVariableLength() {
        byte[] bytes = new byte[512];
        new Random(23).nextBytes(bytes);
        for (int i = 0; i <= bytes.length; i++) {
            int expected = MurmurHash3Yonik.murmurhash3_x86_32(bytes, 0, i, 0);
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
            int expected = MurmurHash3Yonik.murmurhash3_x86_32(bytes, i, bytes.length - i, 0);
            checksum.reset();
            checksum.update(bytes, i, bytes.length - i);
            int value = (int) checksum.getValue();
            Assert.assertEquals(expected, value);
        }
    }

    @Test
    public void testExpectedHash() {
        super.testExpectedHash(0x2362f9deL, 0xd49eb151L, 0x46a9cdc1L);
    }

    @Test
    public void testSeed() {
        Random random = new Random(511);
        for (int i = 0; i <= 512; i++) {
            int seed = random.nextInt();
            int expected = MurmurHash3Yonik.murmurhash3_x86_32(INPUT4, 0, INPUT4.length, seed);
            checksum = new Murmur3A(seed);
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
            assertEqualHash(byteBuffer, murmur3a);
        }
    }

    @Test
    public void testUpdateShortAlignment() throws Exception {
        for (int i = 0; i < 16; i++) {
            ByteBuffer byteBuffer = prepareByteBufferLE(i, 5);
            byteBuffer.putShort((short) 12345);
            byteBuffer.putShort((short) -1);
            byteBuffer.put((byte) 23);

            prepareMurmur3aChecksum(i);
            murmur3a.updateShort((short) 12345);
            murmur3a.updateShort((short) -1);  // Also test negative value to check for sign errors
            murmur3a.update((byte) 23); // One more byte to check state is still OK
            assertEqualHash(byteBuffer, murmur3a);
        }
    }

    @Test
    public void testUpdateIntAlignment() throws Exception {
        for (int i = 0; i < 16; i++) {
            ByteBuffer byteBuffer = prepareByteBufferLE(i, 9);
            byteBuffer.putInt(1234567890);
            byteBuffer.putInt(-1);

            byteBuffer.put((byte) 23);

            prepareMurmur3aChecksum(i);
            murmur3a.updateInt(1234567890);
            murmur3a.updateInt(-1); // Also test negative value to check for sign errors
            murmur3a.update((byte) 23); // One more byte to check state is still OK
            assertEqualHash(byteBuffer, murmur3a);
        }
    }

    @Test
    public void testUpdateLongAlignment() throws Exception {
        for (int i = 0; i < 16; i++) {
            ByteBuffer byteBuffer = prepareByteBufferLE(i, 17);
            byteBuffer.putLong(1234567890123456789L);
            byteBuffer.putLong(-1L);
            byteBuffer.put((byte) 23);

            prepareMurmur3aChecksum(i);
            murmur3a.updateLong(1234567890123456789L);
            murmur3a.updateLong(-1L); // Also test negative value to check for sign errors
            murmur3a.update((byte) 23); // One more byte to check state is still OK
            assertEqualHash(byteBuffer, murmur3a);
        }
    }

    @Test
    public void testUpdateShortArray() throws Exception {
        for (int i = 0; i < 16; i++) {
            ByteBuffer byteBuffer = prepareByteBufferLE(i, 16);
            byteBuffer.put(INPUT16);
            long expected = getHash(byteBuffer);

            prepareMurmur3aChecksum(i);
            short[] array = new short[8];
            ByteBuffer.wrap(INPUT16).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(array);
            murmur3a.updateShort(array);
            Assert.assertEquals("Alignment " + i, expected, murmur3a.getValue());
        }
    }

    @Test
    public void testUpdateIntArray() throws Exception {
        for (int i = 0; i < 16; i++) {
            ByteBuffer byteBuffer = prepareByteBufferLE(i, 16, 4);
            Assert.assertEquals(0, byteBuffer.capacity() % 4);
            byteBuffer.put(INPUT16);
            while (byteBuffer.position() < byteBuffer.capacity()) {
                byteBuffer.put((byte) 0);
            }
            long expected = getHash(byteBuffer);

            int[] array = new int[byteBuffer.capacity() / 4];
            byteBuffer.rewind();
            byteBuffer.asIntBuffer().get(array);
            murmur3a.reset();
            murmur3a.updateInt(array);
            Assert.assertEquals(expected, murmur3a.getValue());
        }
    }

    @Test
    public void testUpdateLongArray() throws Exception {
        for (int i = 0; i < 16; i++) {
            ByteBuffer byteBuffer = prepareByteBufferLE(i, 16, 8);
            Assert.assertEquals(0, byteBuffer.capacity() % 8);

            byteBuffer.put(INPUT16);
            while (byteBuffer.position() < byteBuffer.capacity()) {
                byteBuffer.put((byte) 0);
            }
            long expected = getHash(byteBuffer);

            long[] array = new long[byteBuffer.capacity() / 8];
            byteBuffer.rewind();
            byteBuffer.asLongBuffer().get(array);
            murmur3a.reset();
            murmur3a.updateLong(array);
            Assert.assertEquals(expected, murmur3a.getValue());
        }
    }

    @Test
    public void testUpdateMixed() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put((byte) 42);
        byteBuffer.putInt(1234567890);
        byteBuffer.put((byte) 13);
        byteBuffer.put((byte) 23);
        byteBuffer.put((byte) 33);
        byteBuffer.putLong(10000000317777L);
        byteBuffer.put((byte) 99);
        byteBuffer.putShort((short) 666);
        byteBuffer.put((byte) 77);
        byte[] bytes = byteBuffer.array();

        Murmur3A murmur3a = (Murmur3A) checksum;
        murmur3a.update(42);
        murmur3a.updateInt(1234567890);
        murmur3a.update(13);
        murmur3a.update(23);
        murmur3a.update(33);
        murmur3a.updateLong(10000000317777L);
        murmur3a.update(99);
        murmur3a.updateShort((short) 666);
        murmur3a.update(77);
        long value1 = murmur3a.getValue();

        murmur3a.reset();
        murmur3a.update(bytes, 0, bytes.length);
        long value2 = murmur3a.getValue();
        Assert.assertEquals(value2, value1);
    }

    private void prepareMurmur3aChecksum(int prefixLength) {
        murmur3a.reset();
        for (int j = 0; j < prefixLength; j++) {
            murmur3a.update((byte) (0x77 + j));
        }
    }


    private ByteBuffer prepareByteBufferLE(int prefixLength, int additionalLength, int aligmentToPad) {
        int off = (prefixLength + additionalLength) % aligmentToPad;
        int pad = off == 0 ? 0 : aligmentToPad - off;
        return prepareByteBufferLE(prefixLength, additionalLength + pad);
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

    private void assertEqualHash(ByteBuffer byteBuffer, Murmur3A murmur3a) {
        long value = murmur3a.getValue();
        long expected = getHash(byteBuffer);
        Assert.assertEquals("BB capacity: " + byteBuffer.capacity(), expected, value);

        // Sanity check
        if (byteBuffer.capacity() > 0) {
            Assert.assertNotEquals(0, value);
        }
    }


}
