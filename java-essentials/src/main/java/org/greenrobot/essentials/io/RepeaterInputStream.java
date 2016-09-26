/*
 * Copyright (C) 2014-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.greenrobot.essentials.io;

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

    public int read(byte[] b) throws IOException {
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
