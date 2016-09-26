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

import org.greenrobot.essentials.PrimitiveArrayUtils;

import java.util.zip.Checksum;

/**
 * Murmur3A (murmurhash3_x86_32)
 */
public class Murmur3A implements Checksum {
    private static PrimitiveArrayUtils primitiveArrayUtils = PrimitiveArrayUtils.getInstance();

    private static final int C1 = 0xcc9e2d51;
    private static final int C2 = 0x1b873593;

    private final int seed;

    private int h1;
    private int length;

    private int partialK1;
    private int partialK1Pos;

    public Murmur3A() {
        seed = 0;
    }

    public Murmur3A(int seed) {
        this.seed = seed;
        h1 = seed;
    }

    @Override
    public void update(int b) {
        switch (partialK1Pos) {
            case 0:
                partialK1 = 0xff & b;
                partialK1Pos = 1;
                break;
            case 1:
                partialK1 |= (0xff & b) << 8;
                partialK1Pos = 2;
                break;
            case 2:
                partialK1 |= (0xff & b) << 16;
                partialK1Pos = 3;
                break;
            case 3:
                partialK1 |= (0xff & b) << 24;
                applyK1(partialK1);
                partialK1Pos = 0;
                break;
        }
        length++;
    }

    @Override
    public void update(byte[] b, int off, int len) {
        while (partialK1Pos != 0 && len > 0) {
            update(b[off]);
            off++;
            len--;
        }

        int remainder = len & 3;
        int stop = off + len - remainder;
        for (int i = off; i < stop; i += 4) {
            int k1 = primitiveArrayUtils.getIntLE(b, i);
            applyK1(k1);
        }
        length += stop - off;

        for (int i = 0; i < remainder; i++) {
            update(b[stop + i]);
        }
    }

    public void update(byte[] b) {
        update(b, 0, b.length);
    }

    public void updateShort(short value) {
        switch (partialK1Pos) {
            case 0:
                partialK1 = value & 0xffff;
                partialK1Pos = 2;
                break;
            case 1:
                partialK1 |= (value & 0xffff) << 8;
                partialK1Pos = 3;
                break;
            case 2:
                partialK1 |= (value & 0xffff) << 16;
                applyK1(partialK1);
                partialK1Pos = 0;
                break;
            case 3:
                partialK1 |= (value & 0xff) << 24;
                applyK1(partialK1);
                partialK1 = (value >> 8) & 0xff;
                partialK1Pos = 1;
                break;
        }
        length += 2;
    }

    public void updateShort(short... values) {
        int len = values.length;
        if (len > 0 && (partialK1Pos == 0 || partialK1Pos == 2)) {
            // Bulk tweak: for some weird reason this is 25-60% faster than the else block
            int off = 0;
            if (partialK1Pos == 2) {
                partialK1 |= (values[0] & 0xffff) << 16;
                applyK1(partialK1);
                partialK1Pos = 0;
                len--;
                off = 1;
            }

            int joinBeforeIdx = off + (len & 0xfffffffe);
            for (int i = off; i < joinBeforeIdx; i += 2) {
                int joined = (0xffff & values[i]) | ((values[i + 1] & 0xffff) << 16);
                applyK1(joined);
            }
            if (joinBeforeIdx < values.length) {
                partialK1 = values[joinBeforeIdx] & 0xffff;
                partialK1Pos = 2;
            }
            length += 2 * values.length;
        } else {
            for (short value : values) {
                updateShort(value);
            }
        }
    }

    public void updateInt(int value) {
        switch (partialK1Pos) {
            case 0:
                applyK1(value);
                break;
            case 1:
                partialK1 |= (value & 0xffffff) << 8;
                applyK1(partialK1);
                partialK1 = value >>> 24;
                break;
            case 2:
                partialK1 |= (value & 0xffff) << 16;
                applyK1(partialK1);
                partialK1 = value >>> 16;
                break;
            case 3:
                partialK1 |= (value & 0xff) << 24;
                applyK1(partialK1);
                partialK1 = value >>> 8;
                break;
        }
        length += 4;
    }

    public void updateInt(int... values) {
        if (partialK1Pos == 0) {
            // Bulk tweak: for some weird reason this is 25-60% faster than the else block
            for (int value : values) {
                applyK1(value);
            }
            length += 4 * values.length;
        } else {
            for (int value : values) {
                updateInt(value);
            }
        }
    }

    public void updateLong(long value) {
        switch (partialK1Pos) {
            case 0:
                applyK1((int) (value & 0xffffffff));
                applyK1((int) (value >>> 32));
                break;
            case 1:
                partialK1 |= (value & 0xffffff) << 8;
                applyK1(partialK1);
                applyK1((int) ((value >>> 24) & 0xffffffff));
                partialK1 = (int) (value >>> 56);
                break;
            case 2:
                partialK1 |= (value & 0xffff) << 16;
                applyK1(partialK1);
                applyK1((int) ((value >>> 16) & 0xffffffff));
                partialK1 = (int) (value >>> 48);
                break;
            case 3:
                partialK1 |= (value & 0xff) << 24;
                applyK1(partialK1);
                applyK1((int) ((value >>> 8) & 0xffffffff));
                partialK1 = (int) (value >>> 40);
                break;
        }
        length += 8;
    }

    public void updateLong(long... values) {
        if (partialK1Pos == 0) {
            // Bulk tweak: for some weird reason this is ~25% faster than the else block
            for (long value : values) {
                applyK1((int) (value & 0xffffffff));
                applyK1((int) (value >>> 32));
            }
            length += 8 * values.length;
        } else {
            for (long value : values) {
                updateLong(value);
            }
        }
    }

    public void updateFloat(float number) {
        updateInt(Float.floatToIntBits(number));
    }

    public void updateDouble(double number) {
        updateLong(Double.doubleToLongBits(number));
    }

    /** updates a byte with 0 for false and 1 for true */
    public void updateBoolean(boolean value) {
        update(value ? 1 : 0);
    }

    private void applyK1(int k1) {
        k1 *= C1;
        k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
        k1 *= C2;

        h1 ^= k1;
        h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
        h1 = h1 * 5 + 0xe6546b64;
    }

    @Override
    public long getValue() {
        int finished = h1;
        if (partialK1Pos > 0) {
            int k1 = partialK1 * C1;
            k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
            k1 *= C2;
            finished ^= k1;
        }
        finished ^= length;

        // fmix
        finished ^= finished >>> 16;
        finished *= 0x85ebca6b;
        finished ^= finished >>> 13;
        finished *= 0xc2b2ae35;
        finished ^= finished >>> 16;

        return 0xFFFFFFFFL & finished;
    }

    @Override
    public void reset() {
        h1 = seed;
        length = 0;
        partialK1Pos = 0;
    }
}