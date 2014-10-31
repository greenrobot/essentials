package de.greenrobot.common.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Repeats an input stream to an additional OutputStream. The data of the InputStream becomes available for two
 * purposes, e.g. reading data and storing for caching.
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
        int read = super.read();
        if (read > 0) {
            out.write(read);
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);
        if (read > 0) {
            out.write(b, off, read);
        }
        return read;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = super.read();
        if (read > 0) {
            out.write(b, 0, read);
        }
        return read;
    }

}
