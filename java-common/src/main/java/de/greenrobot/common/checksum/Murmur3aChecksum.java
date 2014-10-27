package de.greenrobot.common.checksum;

import de.greenrobot.common.ByteArrayUtils;

import java.util.zip.Checksum;

/**
 * Murmur3A (murmurhash3_x86_32)
 */
public class Murmur3aChecksum implements Checksum {
    private static final int c1 = 0xcc9e2d51;
    private static final int c2 = 0x1b873593;

    private final int seed;

    private int h1;
    private int length;

    private int partialK1;
    private int partialK1Pos;

    public Murmur3aChecksum() {
        seed = 0;
    }

    public Murmur3aChecksum(int seed) {
        this.seed = seed;
        h1 = seed;
    }

    @Override
    public void update(int b) {
        switch (partialK1Pos) {
            case 0:
                partialK1 = 0xff & b;
                break;
            case 1:
                partialK1 |= (0xff & b) << 8;
                break;
            case 2:
                partialK1 |= (0xff & b) << 16;
                break;
            case 3:
                partialK1 |= (0xff & b) << 24;
                break;
        }
        length++;
        if (partialK1Pos == 3) {
            applyK1(partialK1);
            partialK1Pos = 0;
        } else {
            partialK1Pos++;
        }
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
            int k1 = ByteArrayUtils.getIntLE(b, i);
            applyK1(k1);
        }
        length += stop - off;

        for (int i = 0; i < remainder; i++) {
            update(b[stop + i]);
        }
    }

    private void applyK1(int k1) {
        k1 *= c1;
        k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
        k1 *= c2;

        h1 ^= k1;
        h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
        h1 = h1 * 5 + 0xe6546b64;
    }

    @Override
    public long getValue() {
        int finished = h1;
        if (partialK1Pos > 0) {
            int k1 = partialK1 * c1;
            k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
            k1 *= c2;
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