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

import static org.junit.Assert.*;

public class ObjectCacheTest {
    @Test
    public void testBasics() {
        doTestBasics(ObjectCache.ReferenceType.SOFT);
        doTestBasics(ObjectCache.ReferenceType.STRONG);
        doTestBasics(ObjectCache.ReferenceType.WEAK);
    }

    private void doTestBasics(ObjectCache.ReferenceType referenceType) {
        ObjectCache<String, String> cache = new ObjectCache(referenceType, 10, 0);
        String value = "foo";
        String value2 = "bar";
        String key = "mykey";
        assertNull(cache.get(key));
        assertNull(cache.put(key, value));
        assertTrue(cache.containsKey(key));
        assertTrue(cache.containsKeyWithValue(key));
        assertEquals(value, cache.get(key));
        assertEquals(value, cache.put(key, value2));
        assertEquals(value2, cache.get(key));
        assertEquals(value2, cache.remove(key));
        assertNull(value2, cache.get(key));
        assertFalse(cache.containsKey(key));
        assertFalse(cache.containsKeyWithValue(key));
    }


    @Test
    public void testMaxSize() {
        ObjectCache<String, String> cache = createCacheWith4Entries(0);
        cache.put("5", "e");
        assertEquals(4, cache.size());
        assertNull(cache.get("1"));
        assertEquals(cache.get("5"), "e");
        assertEquals(1, cache.getCountEvicted());
    }

    @Test
    public void testEvictToTargetSize() {
        ObjectCache<String, String> cache = createCacheWith4Entries(0);
        cache.evictToTargetSize(2);
        assertEquals(2, cache.size());
        assertEquals(cache.get("3"), "c");
        assertEquals(cache.get("4"), "d");

        cache.evictToTargetSize(0);
        assertEquals(0, cache.size());
    }

    @Test
    public void testExpired() throws InterruptedException {
        ObjectCache<String, String> cache = new ObjectCache(ObjectCache.ReferenceType.STRONG, 4, 1);
        cache.put("1", "a");
        Thread.sleep(3);
        assertNull(cache.get("1"));
        assertEquals(0, cache.size());
        assertEquals(1, cache.getCountExpired());
    }

    @Test
    public void testCleanUpObsoleteEntries() throws InterruptedException {
        // Use more than one entry to detect ConcurrentModificationException
        ObjectCache<String, String> cache = createCacheWith4Entries(1);
        Thread.sleep(3);
        cache.checkCleanUpObsoleteEntries();
        assertEquals(0, cache.size());
        assertEquals(4, cache.getCountExpired());
    }

    @Test
    public void testNotExpired() throws InterruptedException {
        ObjectCache<String, String> cache = new ObjectCache(ObjectCache.ReferenceType.STRONG, 4, 1000);
        cache.put("1", "a");
        Thread.sleep(3);
        assertEquals(cache.get("1"), "a");
        assertEquals(0, cache.getCountExpired());
    }

    private ObjectCache<String, String> createCacheWith4Entries(int expirationMillis) {
        ObjectCache<String, String> cache = new ObjectCache(ObjectCache.ReferenceType.STRONG, 4, expirationMillis);
        cache.put("1", "a");
        cache.put("2", "b");
        cache.put("3", "c");
        cache.put("4", "d");
        assertEquals(4, cache.size());
        return cache;
    }
}
