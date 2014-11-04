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

package de.greenrobot.common.hash;

import org.junit.Assert;
import org.junit.Test;

import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class CombinedChecksumTest extends AbstractChecksumTest{
    public CombinedChecksumTest() {
        super(new CombinedChecksum(new CRC32(), new Adler32()));
    }

    @Test
    public void testCombinedBasics() {
        long emptyValue = checksum.getValue();
        for (int i = 0; i < 256; i++) {
            checksum.update(i);
            long value = checksum.getValue();

            long crc32 = value & 0xffffffff;
            long adler32 = (value >>> 32) & 0xffffffff;

            Assert.assertNotEquals(crc32, adler32);
            Assert.assertNotEquals(0, adler32);
            Assert.assertNotEquals(0, crc32);
        }

        checksum.reset();
        Assert.assertEquals(emptyValue, checksum.getValue());
    }


}
