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

package de.greenrobot.common.io;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class PipelineOutputStreamTest extends TestCase {

    @Before
    public void setUp() throws IOException {
    }

    @Test
    public void testBasics() throws IOException {
        PipelineOutputStream out = new PipelineOutputStream();
        InputStream in = out.getInputStream();
        assertEquals(0, in.available());

        byte[] bytes = createBytes(16);
        out.write(bytes);
        assertEquals(16, in.available());
        assertEquals(1, in.read());
        assertEquals(2, in.read());
        assertEquals(14, in.available());

        byte[] buffer = new byte[16];
        assertEquals(4, in.read(buffer, 1, 4));
        assertEquals(10, in.available());
        assertEquals(3, buffer[1]);
        assertEquals(6, buffer[4]);

        assertEquals(10, in.read(buffer));
        assertEquals(0, in.available());
        assertEquals(7, buffer[0]);
        assertEquals(16, buffer[9]);
        assertEquals(0, buffer[10]);
    }

    @Test
    public void testSkip() throws IOException {
        PipelineOutputStream out = new PipelineOutputStream();
        InputStream in = out.getInputStream();
        byte[] bytes = createBytes(16);
        out.write(bytes);
        in.skip(4);
        assertEquals(12, in.available());
        assertEquals(5, in.read());
    }


    private byte[] createBytes(int len) {
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (i + 1);
        }
        return bytes;
    }
}