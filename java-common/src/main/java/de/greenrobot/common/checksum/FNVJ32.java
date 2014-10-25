package de.greenrobot.common.checksum;

import java.util.zip.Checksum;

/** Hash function based on FNV, but xors 4 bytes after each multiplication (faster). */
public class FNVJ32 implements Checksum {
    private final static int INITIAL_VALUE = 0x811C9DC5;
    private final static int MULTIPLIER = 16777619;

    private int hash = INITIAL_VALUE;

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
        for (int i = off; i < stop; i += 4) {
            hash *= MULTIPLIER;

            //            hash ^= b[i] << 24;
            //            hash ^= (0xff & b[i + 1]) << 16;
            //            hash ^= (0xff & b[i + 2]) << 8;
            //            hash ^= (0xff & b[i + 3]);

            // Using v1-v4 instead of applying directly to hash is faster

            int v1 = b[i] << 24;
            int v2 = (0xff & b[i + 1]) << 16;
            int v3 = (0xff & b[i + 2]) << 8;
            int v4 = (0xff & b[i + 3]);
            hash ^= v1 | v2 | v3 | v4;
        }
    }

    @Override
    public long getValue() {
        return hash & 0xffffffffL;
    }

    @Override
    public void reset() {
        hash = INITIAL_VALUE;
    }
}
