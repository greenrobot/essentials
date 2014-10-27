package de.greenrobot.common.checksum;

import de.greenrobot.common.ByteArrayUtils;

import java.util.zip.Checksum;

/**
 * Hash function with emphasis on speed. Test with random data showed pretty good collision behaviour, although
 * quality measured by SMHasher is pretty bad (worse quality than FNV).
 *
 * Based on FNV, but xors 4 bytes at once after each multiplication (faster).
 */
public class FNVJ32 implements Checksum {
    private final static int INITIAL_VALUE = 0x811C9DC5;
    private final static int MULTIPLIER = 16777619;

    private int hash = INITIAL_VALUE;

    private int partialPos;
    private int length;

    @Override
    public void update(int b) {
        if (partialPos == 0) {
            hash *= MULTIPLIER;
        }
        int xorValue = 0xff & b;
        switch (partialPos) {
            case 0:
                xorValue <<= 24;
                break;
            case 1:
                xorValue <<= 16;
                break;
            case 2:
                xorValue <<= 8;
                break;
        }
        hash ^= xorValue;
        partialPos++;
        if (partialPos == 4) {
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
        int remainder = len & 3;
        int stop = off + len - remainder;
        for (int i = off; i < stop; i += 4) {
            hash *= MULTIPLIER;
            // Use big endian: makes it easier to apply partial bytes to hash.
            // Also, for some reason, this results in a better bit distribution quality.
            hash ^= ByteArrayUtils.getIntBE(b, i);
        }
        length += stop - off;

        for (int i = 0; i < remainder; i++) {
            update(b[stop + i]);
        }
    }

    public long getUnfinishedValue() {
        return hash & 0xffffffffL;
    }

    @Override
    public long getValue() {
        int finished = hash * MULTIPLIER;
        finished ^= length;
        finished *= MULTIPLIER;
        return finished & 0xffffffffL;
    }

    public int getLength() {
        return length;
    }

    @Override
    public void reset() {
        hash = INITIAL_VALUE;
        partialPos = 0;
        length = 0;
    }
}
