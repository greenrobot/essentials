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

package org.greenrobot.essentials.hash.otherhashes;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.util.zip.Checksum;

/** TODO */
public class Murmur3fGuavaChecksum implements Checksum {
    Hasher hasher= Hashing.murmur3_128().newHasher();

    @Override
    public void update(int b) {
        hasher.putByte((byte)b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        hasher.putBytes(b,off,len);
    }

    @Override
    public long getValue() {
        return hasher.hash().asLong();
    }

    @Override
    public void reset() {
        hasher= Hashing.murmur3_128().newHasher();
    }
}
