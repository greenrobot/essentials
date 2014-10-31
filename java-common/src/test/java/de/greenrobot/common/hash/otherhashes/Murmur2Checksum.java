package de.greenrobot.common.hash.otherhashes;

import java.util.zip.Checksum;

/** TODO */
public class Murmur2Checksum implements Checksum {
    Long hash;

    @Override
    public void update(int b) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void update(byte[] b, int off, int len) {
        if (hash != null) {
            throw new RuntimeException("No hash building available");
        }
        hash = 0xffffffffL & MurmurHash2.hash(b, 0x9747b28c);
    }

    @Override
    public long getValue() {
        return hash;
    }

    @Override
    public void reset() {
        hash = null;
    }
}
