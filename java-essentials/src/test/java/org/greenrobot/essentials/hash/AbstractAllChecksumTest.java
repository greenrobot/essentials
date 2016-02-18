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

import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public abstract class AbstractAllChecksumTest {
    @Parameterized.Parameter
    public Checksum checksum;

    @Parameterized.Parameters(name = "{0}")
    public static Collection alignments() {
        return Arrays.asList(new Object[][]{
                {new Adler32()},
                {new FNV32()},
                {new FNV64()},
                {new Murmur3A()},
                {new Murmur3F()}});
    }
}
