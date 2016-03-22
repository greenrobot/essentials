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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class StringUtilsTest {
    private final static String LINES = "Line 1\nLine 2\n\nLine 4\r\nLine 5\r\n\r\nLine 7";

    @Test
    public void testSplitLines() {
        String[] lines = StringUtils.splitLines(LINES, false);
        assertEquals(7, lines.length);

        assertEquals("Line 1", lines[0]);
        assertEquals("Line 2", lines[1]);
        assertEquals("", lines[2]);
        assertEquals("Line 4", lines[3]);
        assertEquals("Line 5", lines[4]);
        assertEquals("", lines[5]);
        assertEquals("Line 7", lines[6]);
    }

    @Test
    public void testSplitLinesSkipEmptyLines() {
        String[] lines = StringUtils.splitLines(LINES, true);
        assertEquals(5, lines.length);

        assertEquals("Line 1", lines[0]);
        assertEquals("Line 2", lines[1]);
        assertEquals("Line 4", lines[2]);
        assertEquals("Line 5", lines[3]);
        assertEquals("Line 7", lines[4]);
    }

    @Test
    public void testSplit() throws Exception {
        assertArrayEquals(ss("John", "Doe"), StringUtils.split("John Doe", ' '));
        assertArrayEquals(ss("John", "", "Doe", ""), StringUtils.split("John  Doe ", ' '));
        assertArrayEquals(ss("", "John", "Doe", ""), StringUtils.split(" John Doe ", ' '));
        assertArrayEquals(ss("John", "Christoph", "Doe"), StringUtils.split("John Christoph Doe", ' '));
        assertArrayEquals(ss("John", "", "", "Doe"), StringUtils.split("John,,,Doe", ','));
        assertArrayEquals(ss("John", "Doe", ""), StringUtils.split("John Doe ", ' '));
        assertArrayEquals(ss("John", "", "", ""), StringUtils.split("John,,,", ','));
    }

    private String[] ss(String... values) {
        return values;
    }

    @Test
    public void testFindLinesContaining() {
        String text = "LiXXXne 1\nLine 2\n\nLXXXine 4\r\nLine 5\r\nXXX\r\nLine 7";
        List<String> lines = StringUtils.findLinesContaining(text, "XXX");
        assertEquals(3, lines.size());

        assertEquals("LiXXXne 1", lines.get(0));
        assertEquals("LXXXine 4", lines.get(1));
        assertEquals("XXX", lines.get(2));
    }

    @Test
    public void testConcatLines() {
        String[] lines = StringUtils.splitLines(LINES, false);
        ArrayList<String> list = new ArrayList<String>();
        for (String line : lines) {
            list.add(line);
        }
        String concated = StringUtils.join(list, "\n");
        assertEquals("Line 1\nLine 2\n\nLine 4\nLine 5\n\nLine 7", concated);
    }

    @Test
    public void testJoinIterable() {
        assertEquals("", StringUtils.join((Iterable) null, "blub"));
        List<String> fooBarList = Arrays.asList("foo", "bar");
        assertEquals("foo,bar", StringUtils.join(fooBarList, ","));
        assertEquals("foo, bar", StringUtils.join(fooBarList, ", "));
    }

    @Test
    public void testJoinIntArray() {
        assertEquals("", StringUtils.join((int[]) null, "blub"));
        int[] ints = {42, 23};
        assertEquals("42,23", StringUtils.join(ints, ","));
        assertEquals("42, 23", StringUtils.join(ints, ", "));
    }

    @Test
    public void testJoinStringArray() {
        assertEquals("", StringUtils.join((String[]) null, "blub"));
        String[] fooBar = {"foo", "bar"};
        assertEquals("foo,bar", StringUtils.join(fooBar, ","));
        assertEquals("foo, bar", StringUtils.join(fooBar, ", "));
    }

    @Test
    public void testEllipsize() {
        assertEquals("He...", StringUtils.ellipsize("Hello world", 5));
        assertEquals("Hell>", StringUtils.ellipsize("Hello world", 5, ">"));
    }

    @Test
    public void testHex() {
        assertArrayEquals(new byte[] {0, 0x66, -1}, StringUtils.parseHex("0066FF"));
    }

}
