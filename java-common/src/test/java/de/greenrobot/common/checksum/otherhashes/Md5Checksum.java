package de.greenrobot.common.checksum.otherhashes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Checksum;

/** TODO */
public class Md5Checksum implements Checksum {
    private final MessageDigest digest;

    public Md5Checksum() {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int b) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void update(byte[] b, int off, int len) {
        digest.update(b, off, len);
    }

    @Override
    public long getValue() {
        return digest.digest()[0]; // Just return something
    }

    @Override
    public void reset() {
        digest.reset();
    }
}
