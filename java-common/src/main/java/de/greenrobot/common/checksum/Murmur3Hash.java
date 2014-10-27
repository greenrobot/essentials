package de.greenrobot.common.checksum;

import de.greenrobot.common.ByteArrayUtils;

/**
 * The MurmurHash3 algorithm was created by Austin Appleby.  This java port was authored by
 * Yonik Seeley and is placed into the public domain.  The author hereby disclaims copyright
 * to this source code.
 * <p/>
 * This produces exactly the same hash values as the final C++
 * version of MurmurHash3 and is thus suitable for producing the same hash values across
 * platforms.
 * <p/>
 * The 32 bit x86 version of this hash should be the fastest variant for relatively short keys like ids.
 * <p/>
 * Note - The x86 and x64 versions do _not_ produce the same results, as the
 * algorithms are optimized for their respective platforms.
 * <p/>
 * See http://github.com/yonik/java_util for future updates to this file.
 */
public class Murmur3Hash {

    /** Returns the MurmurHash3_x86_32 hash. */
    public static int murmurhash3_x86_32(byte[] data, int offset, int len, int seed) {
        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;

        int h1 = seed;
        int roundedEnd = offset + (len & 0xfffffffc);  // round down to 4 byte block

        for (int i = offset; i < roundedEnd; i += 4) {
            int k1 = ByteArrayUtils.getIntLE(data, i);
            k1 *= c1;
            k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
            k1 *= c2;

            h1 ^= k1;
            h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
            h1 = h1 * 5 + 0xe6546b64;
        }

        // tail
        int k1 = 0;

        switch (len & 0x03) {
            case 3:
                k1 = (data[roundedEnd + 2] & 0xff) << 16;
                // fallthrough
            case 2:
                k1 |= (data[roundedEnd + 1] & 0xff) << 8;
                // fallthrough
            case 1:
                k1 |= (data[roundedEnd] & 0xff);
                k1 *= c1;
                k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                k1 *= c2;
                h1 ^= k1;
        }

        // finalization
        h1 ^= len;

        // fmix(h1);
        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;

        return h1;
    }


    /**
     * Returns the MurmurHash3_x86_32 hash of the UTF-8 bytes of the String without actually encoding
     * the string to a temporary buffer.  This is more than 2x faster than hashing the result
     * of String.getBytes().
     */
    public static int murmurhash3_x86_32(CharSequence data, int offset, int len, int seed) {

        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;

        int h1 = seed;

        int pos = offset;
        int end = offset + len;
        int k1 = 0;
        int k2 = 0;
        int shift = 0;
        int bits = 0;
        int nBytes = 0;   // length in UTF8 bytes


        while (pos < end) {
            int code = data.charAt(pos++);
            if (code < 0x80) {
                k2 = code;
                bits = 8;

                /***
                 // optimized ascii implementation (currently slower!!! code size?)
                 if (shift == 24) {
                 k1 = k1 | (code << 24);

                 k1 *= c1;
                 k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                 k1 *= c2;

                 h1 ^= k1;
                 h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
                 h1 = h1*5+0xe6546b64;

                 shift = 0;
                 nBytes += 4;
                 k1 = 0;
                 } else {
                 k1 |= code << shift;
                 shift += 8;
                 }
                 continue;
                 ***/

            } else if (code < 0x800) {
                k2 = (0xC0 | (code >> 6))
                        | ((0x80 | (code & 0x3F)) << 8);
                bits = 16;
            } else if (code < 0xD800 || code > 0xDFFF || pos >= end) {
                // we check for pos>=end to encode an unpaired surrogate as 3 bytes.
                k2 = (0xE0 | (code >> 12))
                        | ((0x80 | ((code >> 6) & 0x3F)) << 8)
                        | ((0x80 | (code & 0x3F)) << 16);
                bits = 24;
            } else {
                // surrogate pair
                // int utf32 = pos < end ? (int) data.charAt(pos++) : 0;
                int utf32 = (int) data.charAt(pos++);
                utf32 = ((code - 0xD7C0) << 10) + (utf32 & 0x3FF);
                k2 = (0xff & (0xF0 | (utf32 >> 18)))
                        | ((0x80 | ((utf32 >> 12) & 0x3F))) << 8
                        | ((0x80 | ((utf32 >> 6) & 0x3F))) << 16
                        | (0x80 | (utf32 & 0x3F)) << 24;
                bits = 32;
            }


            k1 |= k2 << shift;

            // int used_bits = 32 - shift;  // how many bits of k2 were used in k1.
            // int unused_bits = bits - used_bits; //  (bits-(32-shift)) == bits+shift-32  == bits-newshift

            shift += bits;
            if (shift >= 32) {
                // mix after we have a complete word

                k1 *= c1;
                k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                k1 *= c2;

                h1 ^= k1;
                h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
                h1 = h1 * 5 + 0xe6546b64;

                shift -= 32;
                // unfortunately, java won't let you shift 32 bits off, so we need to check for 0
                if (shift != 0) {
                    k1 = k2 >>> (bits - shift);   // bits used == bits - newshift
                } else {
                    k1 = 0;
                }
                nBytes += 4;
            }

        } // inner

        // handle tail
        if (shift > 0) {
            nBytes += shift >> 3;
            k1 *= c1;
            k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
            k1 *= c2;
            h1 ^= k1;
        }

        // finalization
        h1 ^= nBytes;

        // fmix(h1);
        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;

        return h1;
    }

}