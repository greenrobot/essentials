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

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class LongHashMapTest {

    Random random;
    private String traceName;
    private long start;

    public LongHashMapTest() {
        this.random = new Random();
    }

    @Test
    public void testLongHashMapSimple() {
        LongHashMap<Object> map = new LongHashMap<Object>();

        map.put(1l << 33, "OK");
        assertNull(map.get(0));
        assertEquals("OK", map.get(1l << 33));

        long keyLong = 0x7fffffffl << 33l + 14;
        assertNull(map.remove(keyLong));
        map.put(keyLong, "OK");
        assertTrue(map.containsKey(keyLong));
        assertEquals("OK", map.remove(keyLong));

        keyLong = Long.MAX_VALUE;
        map.put(keyLong, "OK");
        assertTrue(map.containsKey(keyLong));

        keyLong = 8064216579113853113l;
        map.put(keyLong, "OK");
        assertTrue(map.containsKey(keyLong));
    }

    @Test
    public void testLongHashMapRandom() {
        LongHashMap<Object> map = new LongHashMap<Object>();
        for (int i = 0; i < 5000; i++) {
            long key = random.nextLong();
            String value = "Value-" + key;
            map.put(key, value);
            assertTrue("" + key, map.containsKey(key));

            int keyInt = (int) key;
            String valueInt = "Value-" + keyInt;
            map.put(keyInt, valueInt);
            assertTrue(map.containsKey(keyInt));

            assertEquals(value, map.get(key));
            assertEquals(valueInt, map.get(keyInt));

            assertEquals(value, map.remove(key));
            assertEquals(valueInt, map.remove(keyInt));

            assertNull(map.get(key));
            assertNull(map.get(keyInt));
        }
    }

}
