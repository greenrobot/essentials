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

public class FNVJ32Test extends AbstractChecksumTest {
    public FNVJ32Test() {
        super(new FNVJ32());
    }

    @Test
    public void testExpectedHash() {
        testExpectedHash(0x46B0F16BL, 0x252B0A1FL, 0x8AF88E69L);
    }

    @Test
    public void testSeed() {
        checksum.update(23);
        long value = checksum.getValue();

        FNVJ32 fnvj = new FNVJ32(1);
        fnvj.update(23);
        long valueSeeded = fnvj.getValue();
        Assert.assertNotEquals(value, valueSeeded);

        fnvj.reset();
        fnvj.update(23);
        Assert.assertEquals(valueSeeded, fnvj.getValue());
    }

}
