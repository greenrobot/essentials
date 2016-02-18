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

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class StringUtilsTest extends TestCase {
    private final static String LINES = "Line 1\nLine 2\n\nLine 4\r\nLine 5\r\n\r\nLine 7";

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

    public void testSplitLinesSkipEmptyLines() {
        String[] lines = StringUtils.splitLines(LINES, true);
        assertEquals(5, lines.length);

        assertEquals("Line 1", lines[0]);
        assertEquals("Line 2", lines[1]);
        assertEquals("Line 4", lines[2]);
        assertEquals("Line 5", lines[3]);
        assertEquals("Line 7", lines[4]);
    }

    public void testFindLinesContaining() {
        String text = "LiXXXne 1\nLine 2\n\nLXXXine 4\r\nLine 5\r\nXXX\r\nLine 7";
        List<String> lines = StringUtils.findLinesContaining(text, "XXX");
        assertEquals(3, lines.size());

        assertEquals("LiXXXne 1", lines.get(0));
        assertEquals("LXXXine 4", lines.get(1));
        assertEquals("XXX", lines.get(2));
    }

    public void testConcatLines() {
        String[] lines = StringUtils.splitLines(LINES, false);
        ArrayList<String> list = new ArrayList<String>();
        for (String line : lines) {
            list.add(line);
        }
        String concated = StringUtils.concatLines(list);
        assertEquals("Line 1\nLine 2\n\nLine 4\nLine 5\n\nLine 7", concated);
    }


    // // Not really public so far
    // public void __testEmail() {
    // assertTrue(StringUtils.__isValidEmail("test@greenrobot.de"));
    // assertTrue(StringUtils.__isValidEmail("t_e_s_t_1_2_3_4@greenrobot.de"));
    // assertFalse(StringUtils.__isValidEmail("de"));
    // }
}
