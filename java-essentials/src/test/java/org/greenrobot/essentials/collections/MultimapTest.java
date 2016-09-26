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

package org.greenrobot.essentials.collections;

import org.greenrobot.essentials.collections.Multimap.ListType;
import org.greenrobot.essentials.collections.MultimapSet.SetType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class MultimapTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{Multimap.create()},
                new Object[]{Multimap.create(ListType.THREAD_SAFE)},
                new Object[]{Multimap.create(ListType.LINKED)},
                new Object[]{MultimapSet.create()},
                new Object[]{MultimapSet.create(SetType.THREAD_SAFE)}
        );
    }

    @Parameterized.Parameter
    public AbstractMultimap<String, String, ? extends Collection<String>> multimap;

    @Before
    public void setup() {
        multimap.clear();
        multimap.putElement("a", "1");
        multimap.putElement("a", "2");
        multimap.putElement("a", "3");
    }

    @Test
    public void testPutElementAndGet() {
        Collection<String> collection = multimap.get("a");
        assertEquals(3, collection.size());
        if (collection instanceof List) {
            List<String> list = (List<String>) collection;
            assertEquals("1", list.get(0));
            assertEquals("2", list.get(1));
            assertEquals("3", list.get(2));
        }
    }

    @Test
    public void testContains() {
        assertTrue(multimap.containsElement("1"));
        assertFalse(multimap.containsElement("4"));

        assertTrue(multimap.containsElement("a", "1"));
        assertFalse(multimap.containsElement("a", "4"));
    }

    @Test
    public void testRemove() {
        assertTrue(multimap.removeElement("a", "2"));
        assertFalse(multimap.removeElement("a", "2"));

        assertTrue(multimap.removeElement("a", "1"));
        assertTrue(multimap.containsKey("a"));
        assertTrue(multimap.removeElement("a", "3"));
        assertFalse(multimap.containsKey("a"));
    }

    @Test
    public void testPutElements() {
        Collection<String> collection = new HashSet<>();
        collection.add("4");
        collection.add("5");
        assertTrue(multimap.putElements("a", collection));
        assertEquals(5, multimap.get("a").size());
        assertTrue(multimap.containsElement("a", "4"));
        assertTrue(multimap.containsElement("a", "5"));
    }

    @Test
    public void testValuesElements() {
        multimap.putElement("b", "10");
        multimap.putElement("b", "11");

        Collection<String> allStrings = multimap.valuesElements();
        assertEquals(5, allStrings.size());
        assertTrue(allStrings.contains("1"));
        assertTrue(allStrings.contains("10"));
    }

    @Test
    public void testCountElements() {
        multimap.putElement("b", "10");
        multimap.putElement("b", "11");

        assertEquals(5, multimap.countElements());
        assertEquals(3, multimap.countElements("a"));
        assertEquals(2, multimap.countElements("b"));
        assertEquals(0, multimap.countElements("c"));
    }

}
