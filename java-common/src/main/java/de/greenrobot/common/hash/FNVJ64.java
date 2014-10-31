package de.greenrobot.common.hash;

import de.greenrobot.common.PrimitiveArrayUtils;

import java.util.zip.Checksum;

/**
 * Custom 64-bit hash function favoring speed over quality.
 *
 * Tests with random data showed pretty good collision behaviour, although quality measured by SMHasher is pretty bad
 * (much worse than FNV).
 * <p/>
 * If you do progressive updates, update with byte lengths that are a multiple of 8 for best performance.
 * <p/>
 * Based on FNV, but xors 8 bytes at once after each multiplication.
 */
public class FNVJ64 implements Checksum {
    private static PrimitiveArrayUtils primitiveArrayUtils = PrimitiveArrayUtils.getInstance();
    private final static long INITIAL_VALUE = 0xcbf29ce484222325L;
    private final static long MULTIPLIER = 0x100000001b3L;
    private final static int[] PARTIAL_SHIFTS = {56, 48, 40, 32, 24, 16, 8, 0};

    private final long seed;

    private long hash;

    private int partialPos;
    private int length;

    public FNVJ64() {
        hash = seed = INITIAL_VALUE;
    }

    public FNVJ64(long seed) {
        hash = this.seed = INITIAL_VALUE ^ seed;
    }

    @Override
    public void update(int b) {
        if (partialPos == 0) {
            hash *= MULTIPLIER;
        }
        long xorValue = (0xff & b);
        if (partialPos != 7) {
            xorValue <<= PARTIAL_SHIFTS[partialPos];
        }
        hash ^= xorValue;
        partialPos++;
        if (partialPos == 8) {
            partialPos = 0;
        }
        length++;
    }

    @Override
    public void update(byte[] b, int off, int len) {
        while (partialPos != 0 && len > 0) {
            update(b[off]);
            off++;
            len--;
        }
        int remainder = len & 7;
        int stop = off + len - remainder;
        for (int i = off; i < stop; i += 8) {
            hash *= MULTIPLIER;
            // Tests have shown big endian results in a better bit distribution quality
            hash ^= primitiveArrayUtils.getLongBE(b, i);
        }
        length += stop - off;

        for (int i = 0; i < remainder; i++) {
            update(b[stop + i]);
        }
    }

    @Override
    public long getValue() {
        long finished = hash * MULTIPLIER;
        finished ^= length;
        finished *= MULTIPLIER;
        return finished;
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
