package de.greenrobot.common.hash;

import java.util.zip.Checksum;

/** Combines two 32 bit hashes into a 64 bit hash.*/
public class CombinedChecksum implements Checksum {
    private final Checksum checksum1;
    private final Checksum checksum2;

    public CombinedChecksum(Checksum checksum1, Checksum checksum2) {
        this.checksum1 = checksum1;
        this.checksum2 = checksum2;
    }

    @Override
    public void update(int b) {
        checksum1.update(b);
        checksum2.update(b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        checksum1.update(b, off, len);
        checksum2.update(b, off, len);
    }

    @Override
    public long getValue() {
        return (checksum2.getValue() << 32) | (checksum1.getValue() & 0xffffffffL);
    }

    @Override
    public void reset() {
        checksum1.reset();
        checksum2.reset();
    }
}
