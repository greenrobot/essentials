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

import org.greenrobot.essentials.io.IoUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;

/**
 * Tests compatibility with CheckedOutputStream and CheckedInputStream.
 */
@RunWith(Parameterized.class)
public class ChecksumStreamTest extends AbstractAllChecksumTest {
    @Test
    public void testChecksumStreams() throws IOException {
        byte[] content = new byte[33333];
        new Random().nextBytes(content);

        Murmur3F murmur3F = new Murmur3F();
        murmur3F.update(content);
        String hash = murmur3F.getValueHexString();

        murmur3F.reset();
        CheckedOutputStream out = new CheckedOutputStream(new ByteArrayOutputStream(), murmur3F);
        out.write(content);
        Assert.assertEquals(hash, murmur3F.getValueHexString());

        murmur3F.reset();
        CheckedInputStream in = new CheckedInputStream(new ByteArrayInputStream(content), murmur3F);
        IoUtils.readAllBytes(in);
        Assert.assertEquals(hash, murmur3F.getValueHexString());
    }

}
