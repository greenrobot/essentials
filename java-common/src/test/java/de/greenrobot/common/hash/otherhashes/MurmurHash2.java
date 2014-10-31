package de.greenrobot.common.hash.otherhashes;

// Ported by Derek Young from the C version (specifically the endian-neutral
// version) from:
//   http://murmurhash.googlepages.com/
//
// released to the public domain - dmy999@gmail.com
public class MurmurHash2 {

    @SuppressWarnings("fallthrough")
        public static int hash(byte[] data, int seed) {
        // 'm' and 'r' are mixing constants generated offline.
        // They're not really 'magic', they just happen to work well.
        int m = 0x5bd1e995;
        int r = 24;

        // Initialize the hash to a 'random' value
        int len = data.length;
        int h = seed ^ len;

        int i = 0;
        while (len  >= 4) {
            int k = data[i + 0] & 0xFF;
            k |= (data[i + 1] & 0xFF) << 8;
            k |= (data[i + 2] & 0xFF) << 16;
            k |= (data[i + 3] & 0xFF) << 24;

            k *= m;
            k ^= k >>> r;
            k *= m;

            h *= m;
            h ^= k;

            i += 4;
            len -= 4;
        }

        switch (len) {
        case 3: h ^= (data[i + 2] & 0xFF) << 16;
        case 2: h ^= (data[i + 1] & 0xFF) << 8;
        case 1: h ^= (data[i + 0] & 0xFF);
                h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }

}