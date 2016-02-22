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

package org.greenrobot.essentials.io;

import org.greenrobot.essentials.hash.Murmur3F;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/** Implicitly tests some of IoUtils. */
public class FileUtilsTest {

    private File file;

    @Before
    public void setUp() throws IOException {
        file = File.createTempFile("file-utils-test", ".txt");
        file.deleteOnExit();
    }

    @Test
    public void testWriteAndReadUtf8() throws IOException {
        String text = "Hello, let's put in some Umlauts: öäüÖÄÜ €";
        FileUtils.writeUtf8(file, text);
        Assert.assertEquals(text, FileUtils.readUtf8(file));
    }

    @Test
    public void testAppendUtf8() throws IOException {
        String text = "Hello";
        FileUtils.writeUtf8(file, text);
        FileUtils.appendUtf8(file, " world");
        Assert.assertEquals("Hello world", FileUtils.readUtf8(file));
    }

    @Test
    public void testWriteAndReadObject() throws Exception {
        String text = "Hello, let's put in some Umlauts: öäüÖÄÜ €";
        String text2 = "And one more";
        ArrayList<String> strings = new ArrayList<String>();
        strings.add(text);
        strings.add(text2);
        FileUtils.writeObject(file, strings);
        ArrayList<String> strings2 = (ArrayList<String>) FileUtils.readObject(file);
        Assert.assertEquals(strings.size(), strings2.size());
        Assert.assertEquals(text, strings2.get(0));
        Assert.assertEquals(text2, strings2.get(1));
    }

    @Test
    public void testDigestMd5AndSha1() throws IOException, ClassNotFoundException {
        byte[] content = new byte[33333];
        new Random(42).nextBytes(content);
        FileUtils.writeBytes(file, content);

        Assert.assertEquals("E4DB2A1C03CA891DDDCE45150570ABEB", FileUtils.getMd5(file).toUpperCase());
        Assert.assertEquals("5123C97498170FFA46056190D9439DA203E5234C", FileUtils.getSha1(file).toUpperCase());
    }

    @Test
    public void testUpdateChecksumAndCopy() throws IOException, ClassNotFoundException {
        byte[] content = new byte[33333];
        new Random().nextBytes(content);

        Murmur3F murmur3F = new Murmur3F();
        murmur3F.update(content);
        String hash = murmur3F.getValueHexString();

        FileUtils.writeBytes(file, content);

        File file2 = File.createTempFile("file-utils-test", ".txt");
        file2.deleteOnExit();
        FileUtils.copyFile(file, file2);

        murmur3F.reset();
        FileUtils.updateChecksum(file, murmur3F);
        Assert.assertEquals(hash, murmur3F.getValueHexString());
    }

}
