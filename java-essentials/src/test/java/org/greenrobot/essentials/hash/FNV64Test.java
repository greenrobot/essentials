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
import org.junit.Test;

public class FNV64Test extends  AbstractChecksumTest {
    private final static byte[] INPUT64_ZERO = {(byte) 0xd5, 0x6b, (byte) 0xb9, 0x53, 0x42, (byte) 0x87, 0x08, 0x36};

    public FNV64Test() {
        super(new FNV64());
    }

    @Test
    public void testFnv64UpdateZeroHash() {
        for (int b : INPUT64_ZERO) {
            checksum.update(b);
        }
        Assert.assertEquals(0, checksum.getValue());
    }

    @Test
    public void testFnv64UpdateBytesZeroHash() {
        checksum.update(INPUT64_ZERO, 0, INPUT64_ZERO.length);
        Assert.assertEquals(0, checksum.getValue());
    }

}
