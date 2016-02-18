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
