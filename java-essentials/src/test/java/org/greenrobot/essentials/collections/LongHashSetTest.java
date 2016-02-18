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

package org.greenrobot.essentials.collections;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LongHashSetTest {

    Random random;
    private String traceName;
    private long start;

    public LongHashSetTest() {
        this.random = new Random();
    }

    @Test
    public void testLongHashSetSimple() {
        LongHashSet set = new LongHashSet();

        set.add(1l << 33);
        assertFalse(set.contains(0));
        assertTrue(set.contains(1l << 33));

        long keyLong = 0x7fffffffl << 33l + 14;
        assertFalse(set.remove(keyLong));
        set.add(keyLong);
        assertTrue(set.contains(keyLong));
        assertTrue(set.remove(keyLong));
        assertFalse(set.remove(keyLong));

        keyLong = Long.MAX_VALUE;
        set.add(keyLong);
        assertTrue(set.contains(keyLong));

        keyLong = 8064216579113853113l;
        set.add(keyLong);
        assertTrue(set.contains(keyLong));
    }

    @Test
    public void testLongHashMapRandom() {
        LongHashSet set = new LongHashSet();
        for (int i = 0; i < 5000; i++) {
            long key = random.nextLong();
            set.add(key);
            assertTrue(set.contains(key));

            int keyInt = (int) key;
            set.add(keyInt);
            assertTrue(set.contains(keyInt));

            assertTrue(set.remove(key));
            if(key!=keyInt) {
                assertTrue(set.remove(keyInt));
            }

            assertFalse(set.remove(key));
            assertFalse(set.remove(keyInt));
        }
    }

}
