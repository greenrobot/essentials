package de.greenrobot.common.hash.otherhashes;

import de.greenrobot.common.PrimitiveArrayUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Checksum;

/** Warpper for MessageDigest. */
public class MessageDigestChecksum implements Checksum {
    private final MessageDigest digest;
    private PrimitiveArrayUtils primitiveArrayUtils = PrimitiveArrayUtils.getInstance();

    public MessageDigestChecksum(MessageDigest digest) {
        this.digest = digest;
    }

    public MessageDigestChecksum(String algo) {
        try {
            digest = (MessageDigest.getInstance(algo));
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
        return primitiveArrayUtils.getLongLE(digest.digest(), 0);
    }

    @Override
    public void reset() {
        digest.reset();
    }
}
