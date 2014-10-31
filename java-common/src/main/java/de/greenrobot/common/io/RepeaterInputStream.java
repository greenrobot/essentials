package de.greenrobot.common.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Repeats an input stream to an additional OutputStream. The data of the InputStream becomes available for two
 * purposes, e.g. reading data and storing for caching.
 * <p/>
 * Note: OutputStream is not closed when close() is called.
 *
 * @author Markus
 */
public class RepeaterInputStream extends FilterInputStream {
    private final OutputStream out;

    public RepeaterInputStream(InputStream in, OutputStream out) {
        super(in);
        this.out = out;
    }

    @Override
    public int read() throws IOException {
        int read = in.read();
        if (read > 0) {
            out.write(read);
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = in.read(b, off, len);
        if (read > 0) {
            out.write(b, off, read);
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

}
