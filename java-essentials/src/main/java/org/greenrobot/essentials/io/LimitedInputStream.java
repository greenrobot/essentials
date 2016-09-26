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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Wraps around an InputStream and limits the amount of bytes that can be read from it. Use it if you operate on an
 * InputStream that consists of chunks of a know size, each to be processed using a <b>buffered</b> input stream (e.g.
 * GZIPInputStream). Normal buffered input streams would read beyond the limit. The LimitedInputStream never closes the
 * inside InputStream(close does nothing).
 *
 * @author Markus
 */
public class LimitedInputStream extends InputStream {

    public static GZIPInputStream createGZIPInputStream(InputStream in, int maxBytes) throws IOException {
        LimitedInputStream limitedInputStream = new LimitedInputStream(in, maxBytes);
        return new GZIPInputStream(limitedInputStream);
    }

    private int bytesLeft;
    private final InputStream in;

    public LimitedInputStream(InputStream in, int maxBytes) {
        this.in = in;
        bytesLeft = maxBytes;
    }

    @Override
    public int available() throws IOException {
        int available = in.available();
        return Math.min(available, bytesLeft);
    }

    @Override
    public int read() throws IOException {
        if (bytesLeft <= 0) {
            return -1;
        }
        int read = in.read();
        if (read != -1) {
            bytesLeft--;
        }
        return read;
    }

    @Override
    public int read(byte[] buffer, int offset, int count) throws IOException {
        if (bytesLeft <= 0) {
            return -1;
        }
        int countToRead = Math.min(bytesLeft, count);
        int read = in.read(buffer, offset, countToRead);
        if (read > 0) {
            bytesLeft -= read;
        }
        return read;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    @Override
    public long skip(long byteCount) throws IOException {
        if (byteCount <= 0) {
            return 0;
        }
        long countToSkip = Math.min(bytesLeft, byteCount);
        long skipped = in.skip(countToSkip);
        if (skipped > 0) {
            bytesLeft -= skipped;
        }
        return skipped;
    }

    @Override
    /** Does nothing, because the inner stream is intended to be left open and closed separately (by design).*/
    public void close() throws IOException {
    }

    public int getBytesLeft() {
        return bytesLeft;
    }
    
}
