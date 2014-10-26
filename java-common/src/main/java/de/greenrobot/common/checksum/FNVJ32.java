package de.greenrobot.common.checksum;

import java.util.zip.Checksum;

/** Hash function based on FNV, but xors 4 bytes after each multiplication (faster). */
public class FNVJ32 implements Checksum {
    private final static int INITIAL_VALUE = 0x811C9DC5;
    private final static int MULTIPLIER = 16777619;

    private int hash = INITIAL_VALUE;

    private int pos;
    private int length;

    @Override
    public void update(int b) {
        if (pos == 0) {
            hash *= MULTIPLIER;
        }
        int xorValue = 0xff & b;
        switch (pos) {
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
        pos++;
        if (pos == 4) {
            pos = 0;
        }
        length++;
    }

    @Override
    public void update(byte[] b, int off, int len) {
        while (pos != 0 && len > 0) {
            update(b[off]);
            off++;
            len--;
        }
        int remainder = len & 3;
        int stop = off + len - remainder;
        for (int i = off; i < stop; i += 4) {
            hash *= MULTIPLIER;

            // Using v1-v4 instead of applying directly to hash is faster
            int v1 = b[i] << 24;
            int v2 = (0xff & b[i + 1]) << 16;
            int v3 = (0xff & b[i + 2]) << 8;
            int v4 = (0xff & b[i + 3]);
            hash ^= v1 | v2 | v3 | v4;
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
        pos = 0;
        length = 0;
    }
}
