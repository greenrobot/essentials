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

package de.greenrobot.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Replacement for Java's PipedOutputStream: all data written to this stream will get available in the integrated
 * InputStream.
 * <p/>
 * Note:You should use exactly two threads: one to write and one to read. If you use a single thread, avoid reading
 * more bytes than previously written or writing more bytes than the internal buffer can handle.
 */
public class PipelineOutputStream extends OutputStream {
    private final PipelineInputStream inputStream;
    final CircularByteBuffer buffer;

    public PipelineOutputStream() {
        this(8192);
    }

    public PipelineOutputStream(int bufferCapacity) {
        this.buffer = new CircularByteBuffer(bufferCapacity);
        inputStream = new PipelineInputStream();
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public synchronized void write(byte[] data, int off, int len) throws IOException {
        int done = 0;
        while (done != len) {
            int count = buffer.put(data, off + done, len - done);
            if (count > 0) {
                done += count;
                notifyBuffer();
            } else {
                waitForBuffer();
            }
        }
    }

    @Override
    public synchronized void write(int b) throws IOException {
        while (!buffer.put((byte) b)) {
            waitForBuffer();
        }
        notifyBuffer();
    }

    void waitForBuffer() throws IOException {
        try {
            wait();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    void notifyBuffer() {
        notifyAll();
    }

    protected class PipelineInputStream extends InputStream {

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            }
            int read;
            synchronized (PipelineOutputStream.this) {
                do {
                    read = buffer.get(b, off, len);
                    if (read == 0) {
                        waitForBuffer();
                    }
                } while (read == 0);
                notifyBuffer();
            }

            return read;
        }

        @Override
        public int read() throws IOException {
            synchronized (PipelineOutputStream.this) {
                int value = buffer.get();
                while (value == -1) {
                    waitForBuffer();
                    value = buffer.get();
                }
                notifyBuffer();
                return value;
            }
        }

        @Override
        public int available() throws IOException {
            return buffer.available();
        }

        @Override
        public long skip(long n) throws IOException {
            int len = (int) Math.min(n, Integer.MAX_VALUE);
            int total = 0;
            synchronized (PipelineOutputStream.this) {
                while (total < len) {
                    int skipped = buffer.skip(len - total);
                    if (skipped == 0) {
                        waitForBuffer();
                    } else {
                        total += skipped;
                        notifyBuffer();
                    }
                }
                return total;
            }
        }
    }

}
