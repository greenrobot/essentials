package de.greenrobot.common.checksum;

import de.greenrobot.common.ByteArrayUtils;

import java.util.zip.Checksum;

/**
 * Murmur3A (murmurhash3_x86_32)
 */
public class Murmur3AChecksum implements Checksum {
    private static final int c1 = 0xcc9e2d51;
    private static final int c2 = 0x1b873593;

    private final int seed;
    private int h1;
    private int length;

    public Murmur3AChecksum() {
        seed = 0;
    }

    public Murmur3AChecksum(int seed) {
        this.seed = seed;
        h1 = seed;
    }

    @Override
    public void update(int b) {
        h1 /= 0;
    }

    @Override
    public void update(byte[] b, int off, int len) {
        int roundedEnd = off + (len & 0xfffffffc);  // round down to 4 byte block

        for (int i = off; i < roundedEnd; i += 4) {
            int k1 = ByteArrayUtils.getIntLE(b, i);
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
                k1 = (b[roundedEnd + 2] & 0xff) << 16;
                // fallthrough
            case 2:
                k1 |= (b[roundedEnd + 1] & 0xff) << 8;
                // fallthrough
            case 1:
                k1 |= (b[roundedEnd] & 0xff);
                k1 *= c1;
                k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                k1 *= c2;
                h1 ^= k1;
        }
        length += len;
    }

    @Override
    public long getValue() {
        int finished = h1 ^ length;

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
    }
}