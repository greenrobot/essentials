package de.greenrobot.common.checksum;

import de.greenrobot.common.ByteArrayUtils;

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
            // Use big endian: makes it easier to apply partial bytes to hash.
            // Also, for some reason, this results in a better bit distribution quality.
            hash ^= ByteArrayUtils.getLongBE(b, i);
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
