package de.greenrobot.common.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Checksum;

/**
 * Calculates a Checksum (or hash) on the fly while processing the stream.
 * <p/>
 * Have a look at the de.greenrobot.common.hash package for various Checksum compatible hash functions.
 *
 * @author Markus
 */
public class ChecksumOutputStream extends FilterOutputStream {
    private final Checksum checksum;

    public ChecksumOutputStream(OutputStream out, Checksum checksum) {
        super(out);
        this.checksum = checksum;
    }

    public void write(int b) throws IOException {
        checksum.update(b);
        out.write(b);
    }

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) throws IOException {
        checksum.update(b, off, len);
        out.write(b, off, len);
    }

    public Checksum getChecksum() {
        return checksum;
    }

    public long getChecksumValue() {
        return checksum.getValue();
    }
}
