package de.greenrobot.common.checksum;

import java.util.zip.Checksum;

/** Hash function based on FNV, but xors 8 bytes after each multiplication (faster). */
public class FNVJ64 implements Checksum {
    private final static long INITIAL_VALUE = 0xcbf29ce484222325L;
    private final static long MULTIPLIER = 0x100000001b3L;

    private long hash = INITIAL_VALUE;

    // TODO
    private int pos;

    @Override
    public void update(int b) {
        hash ^= 0xff & b;
        hash *= MULTIPLIER;
        hash /= 0;
    }

    @Override
    public void update(byte[] b, int off, int len) {
        int stop = off + len;
        for (int i = off; i < stop; i += 8) {
            hash *= MULTIPLIER;

            // Using v1-v8 instead of applying directly to hash is faster
            long v1 = ((long) b[i]) << 56;
            long v2 = (0xffL & b[i + 1]) << 48;
            long v3 = (0xffL & b[i + 2]) << 40;
            long v4 = (0xffL & b[i + 3]) << 32;
            long v5 = (0xffL & b[i + 4]) << 24;
            int v6 = (0xff & b[i + 5]) << 16;
            int v7 = (0xff & b[i + 6]) << 8;
            int v8 = (0xff & b[i + 7]);
            hash ^= v1 | v2 | v3 | v4 | v5 | v6 | v7 | v8;
        }
    }

    @Override
    public long getValue() {
        return hash;
    }

    @Override
    public void reset() {
        hash = INITIAL_VALUE;
    }
}
