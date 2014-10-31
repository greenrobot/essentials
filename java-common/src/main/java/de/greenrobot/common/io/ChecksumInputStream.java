package de.greenrobot.common.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Checksum;

/**
 * Calculates a Checksum (or hash) on the fly while processing the stream.
 * <p/>
 * Have a look at the de.greenrobot.common.hash package for various Checksum compatible hash functions.
 *
 * @author Markus
 */
public class ChecksumInputStream extends FilterInputStream {
    private final Checksum checksum;

    public ChecksumInputStream(InputStream in, Checksum checksum) {
        super(in);
        this.checksum = checksum;
    }

    @Override
    public int read() throws IOException {
        int read = in.read();
        if (read > 0) {
            checksum.update(read);
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = in.read(b, off, len);
        if (read > 0) {
            checksum.update(b, off, read);
        }
        return read;
    }

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    /** Unsupported. */
    public boolean markSupported() {
        return false;
    }

    @Override
    /** Unsupported. */
    public void mark(int readlimit) {
    }

    @Override
    /** Unsupported. */
    public void reset() throws IOException {
        throw new IOException("Unsupported");
    }

    public Checksum getChecksum() {
        return checksum;
    }

    public long getChecksumValue() {
        return checksum.getValue();
    }
}
