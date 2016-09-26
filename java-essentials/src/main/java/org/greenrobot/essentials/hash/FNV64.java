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

package org.greenrobot.essentials.hash;

import java.util.zip.Checksum;

/** Hash function FNV-1a (http://www.isthe.com/chongo/tech/comp/fnv). */
public class FNV64 implements Checksum {
    private  final static long INITIAL_VALUE = 0xcbf29ce484222325L;
    private  final static long MULTIPLIER = 0x100000001b3L;

    private long hash = INITIAL_VALUE;

    @Override
    public void update(int b) {
        hash ^= 0xffL & b;
        hash *= MULTIPLIER;
    }

    @Override
    public void update(byte[] b, int off, int len) {
        int stop = off + len;
        for (int i = off; i < stop; i++) {
            hash ^= 0xffL & b[i];
            hash *= MULTIPLIER;
        }
    }

    @Override
    /** Note: Java's long is signed. */
    public long getValue() {
        return hash;
    }

    @Override
    public void reset() {
        hash = INITIAL_VALUE;
    }
}
