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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.zip.Checksum;

public abstract class AbstractChecksumTest {
    protected final static byte[] INPUT4 = {(byte) 0xcc, 0x24, 0x31, (byte) 0xc4};
    protected final static byte[] INPUT16 = {(byte) 0xe0, 0x4d, (byte) 0x9f, (byte) 0xcb, (byte) 0xd5, 0x6b,
            (byte) 0xb9, 0x53, 0x42, (byte) 0x87, 0x08, 0x36, 0x77, 0x23, 0x01, 0};

    protected Checksum checksum;

    protected AbstractChecksumTest(Checksum checksum) {
        this.checksum = checksum;
    }

    @Before
    public void setUp() {
        checksum.reset();
    }

    @Test
    public void testBasics() {
        long initialHash = checksum.getValue();

        for (int b : INPUT4) {
            checksum.update(b);
            Assert.assertNotEquals(initialHash, checksum.getValue());
        }
        long hash = checksum.getValue();

        checksum.reset();
        Assert.assertEquals(initialHash, checksum.getValue());

        checksum.update(INPUT4, 0, INPUT4.length);
        Assert.assertEquals(hash, checksum.getValue());
    }

    @Test
    public void testGetValueStable() {
        checksum.update(INPUT16, 0, INPUT16.length);
        long hash = checksum.getValue();
        // Calling checksum.getValue() twice should not change hash
        Assert.assertEquals(hash, checksum.getValue());
    }

    public void testExpectedHash(long expectedFor0, long expectedForInput4, long expectedForInput16) {
        checksum.update(0);
        checksum.update(0);
        checksum.update(0);
        checksum.update(0);
        Assert.assertEquals("0 (int)", expectedFor0, checksum.getValue());

        checksum.reset();
        checksum.update(INPUT4, 0, INPUT4.length);
        Assert.assertEquals("I4", expectedForInput4, checksum.getValue());

        checksum.reset();
        checksum.update(INPUT16, 0, INPUT16.length);
        Assert.assertEquals("I16", expectedForInput16, checksum.getValue());
    }

    @Test
    public void testRestUnaligned() {
        checksum.update(42);
        long hash = checksum.getValue();
        checksum.reset();
        checksum.update(42);
        Assert.assertEquals(hash, checksum.getValue());
    }

    @Test
    public void testMixedUnaligned() {
        checksum.update(INPUT16, 0, INPUT16.length);
        long hash = checksum.getValue();

        checksum.reset();
        checksum.update(INPUT16, 0, 2);
        checksum.update(INPUT16[2]);
        checksum.update(INPUT16, 3, 11);
        checksum.update(INPUT16[14]);
        checksum.update(INPUT16[15]);
        Assert.assertEquals(hash, checksum.getValue());
    }

    @Test
    public void testTrailingZero() {
        long lastHash = checksum.getValue();
        Assert.assertEquals(0, INPUT16[INPUT16.length - 1]);
        for (int b : INPUT16) {
            checksum.update(b);
            long hash = checksum.getValue();
            Assert.assertNotEquals(lastHash, hash);
            lastHash = hash;
        }
    }

    @Test
    public void testComparePerByteVsByteArray() {
        byte[] bytes = new byte[1024];
        new Random(42).nextBytes(bytes);

        for (int i = 0; i <= bytes.length; i++) {
            checksum.reset();
            for (int j = 0; j < i; j++) {
                checksum.update(bytes[j]);
            }
            long expected = checksum.getValue();

            checksum.reset();
            checksum.update(bytes, 0, i);
            Assert.assertEquals("Iteration " + i, expected, checksum.getValue());
        }

        for (int i = 0; i <= bytes.length; i++) {
            checksum.reset();
            for (int j = i; j < bytes.length ; j++) {
                checksum.update(bytes[j]);
            }
            long expected = checksum.getValue();

            checksum.reset();
            checksum.update(bytes, i, bytes.length - i);
            Assert.assertEquals("Iteration " + i + " (" + (bytes.length - i) + ")", expected, checksum.getValue());
        }

    }

}
