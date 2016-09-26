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

import java.io.UnsupportedEncodingException;
import java.util.zip.Checksum;

/** Wrapper for Checksum that accepts all kind of primitive data to update the hash. */
public class PrimitiveDataChecksum implements Checksum {
    private final Checksum checksum;

    public PrimitiveDataChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    @Override
    public void update(int b) {
        checksum.update(b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        checksum.update(b, off, len);
    }

    @Override
    public long getValue() {
        return checksum.getValue();
    }

    @Override
    public void reset() {
        checksum.reset();
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void updateUtf8(String string) {
        if (string != null) {
            byte[] bytes;
            try {
                bytes = string.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            update(bytes, 0, bytes.length);
        }
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void updateUtf8(String[] strings) {
        if (strings != null) {
            for (String string : strings) {
                updateUtf8(string);
            }
        }
    }

    /** updates a byte with 0 for false and 1 for true */
    public void updateBoolean(boolean value) {
        update(value ? 1 : 0);
    }

    public void updateShort(short number) {
        update((number >>> 8) & 0xff);
        update(number & 0xff);
    }

    public void updateInt(int number) {
        update((number >>> 24) & 0xff);
        update((number >>> 16) & 0xff);
        update((number >>> 8) & 0xff);
        update(number & 0xff);
    }

    public void updateLong(long number) {
        update((int) (number >>> 56) & 0xff);
        update((int) (number >>> 48) & 0xff);
        update((int) (number >>> 40) & 0xff);
        update((int) (number >>> 32) & 0xff);
        update((int) (number >>> 24) & 0xff);
        update((int) (number >>> 16) & 0xff);
        update((int) (number >>> 8) & 0xff);
        update((int) (number & 0xff));
    }

    public void updateFloat(float number) {
        updateInt(Float.floatToIntBits(number));
    }

    public void updateDouble(double number) {
        updateLong(Double.doubleToLongBits(number));
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void update(byte[] numbers) {
        if (numbers != null) {
            update(numbers, 0, numbers.length);
        }
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void update(short[] numbers) {
        if (numbers != null) {
            for (short number : numbers) {
                updateShort(number);
            }
        }
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void update(int[] numbers) {
        if (numbers != null) {
            for (int number : numbers) {
                updateInt(number);
            }
        }
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void update(long[] numbers) {
        if (numbers != null) {
            for (long number : numbers) {
                updateLong(number);
            }
        }
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void update(float[] numbers) {
        if (numbers != null) {
            for (float number : numbers) {
                updateFloat(number);
            }
        }
    }

    /** Note: leaves the checksum untouched if given value is null (provide a special value for stronger hashing). */
    public void update(double[] numbers) {
        if (numbers != null) {
            for (double number : numbers) {
                updateDouble(number);
            }
        }
    }

}
