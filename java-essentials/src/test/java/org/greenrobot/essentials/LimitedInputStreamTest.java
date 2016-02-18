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

import org.greenrobot.essentials.io.LimitedInputStream;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LimitedInputStreamTest {
    @Test
    public void testsBasics() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
        LimitedInputStream limited = new LimitedInputStream(in, 2);
        BufferedInputStream buffered = new BufferedInputStream(limited);

        byte[] readBuffer = new byte[4];
        assertEquals(2, buffered.read(readBuffer));
        assertArrayEquals(new byte[]{1, 2, 0, 0}, readBuffer);
        assertEquals(-1, buffered.read());
    }
}
