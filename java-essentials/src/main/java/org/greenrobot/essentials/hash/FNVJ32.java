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

import org.greenrobot.essentials.PrimitiveArrayUtils;

import java.util.zip.Checksum;

/**
 * Custom 32-bit hash function favoring speed over quality.
 *
 * Tests with random data showed pretty good collision behaviour, although quality measured by SMHasher is pretty bad
 * (much worse than FNV).
 * <p/>
 * If you do progressive updates, update with byte lengths that are a multiple of 4 for best performance.
 * <p/>
 * Based on FNV, but xors 4 bytes at once after each multiplication.
 */
public class FNVJ32 implements Checksum {
    private static PrimitiveArrayUtils primitiveArrayUtils = PrimitiveArrayUtils.getInstance();

    private final static int INITIAL_VALUE = 0x811C9DC5;
    private final static int MULTIPLIER = 16777619;

    private final int seed;

    private int hash = INITIAL_VALUE;

    private int partialPos;
    private int length;

    public FNVJ32() {
        hash = seed = INITIAL_VALUE;
    }

    public FNVJ32(int seed) {
        hash = this.seed = INITIAL_VALUE ^ seed;
    }

    @Override
    public void update(int b) {
        int xorValue = 0xff & b;
        switch (partialPos) {
            case 0:
                hash *= MULTIPLIER;
                xorValue <<= 24;
                partialPos = 1;
                break;
            case 1:
                xorValue <<= 16;
                partialPos = 2;
                break;
            case 2:
                xorValue <<= 8;
                partialPos = 3;
                break;
            case 3:
                partialPos = 0;
                break;
        }
        hash ^= xorValue;
        length++;
    }

    @Override
    public void update(byte[] b, int off, int len) {
        while (partialPos != 0 && len > 0) {
            update(b[off]);
            off++;
            len--;
        }
        int remainder = len & 3;
        int stop = off + len - remainder;
        for (int i = off; i < stop; i += 4) {
            hash *= MULTIPLIER;
            // Tests have shown big endian results in a better bit distribution quality
            hash ^= primitiveArrayUtils.getIntBE(b, i);
        }
        length += stop - off;

        for (int i = 0; i < remainder; i++) {
            update(b[stop + i]);
        }
    }

    @Override
    public long getValue() {
        int finished = hash * MULTIPLIER;
        finished ^= length;
        finished *= MULTIPLIER;
        return finished & 0xffffffffL;
    }

    @Override
    public void reset() {
        hash = seed;
        partialPos = 0;
        length = 0;
    }

    public int getLength() {
        return length;
    }

}
