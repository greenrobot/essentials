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
import java.io.OutputStream;

/**
 * Replacement for Java's PipedOutputStream: all data written to this stream will get available in the integrated
 * InputStream (see {@link #getInputStream()}).
 * <p/>
 * Note: Usually, you will have exactly two threads: one to write and one to read. If you use a single thread, avoid
 * reading more bytes than previously written or writing more bytes than the internal buffer can handle.
 */
public class PipelineOutputStream extends OutputStream {
    private final PipelineInputStream inputStream;
    final CircularByteBuffer buffer;
    boolean closedOut;
    boolean closedIn;

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
            checkPipelineInput();
            int count = buffer.put(data, off + done, len - done);
            if (count > 0) {
                done += count;
                notifyBuffer();
            } else {
                waitForBuffer();
            }
        }
    }

    private void checkPipelineInput() throws IOException {
        if (closedIn) {
            throw new IOException("PipelineInputStream was closed (broken pipeline)");
        }
    }

    @Override
    public synchronized void write(int b) throws IOException {
        checkPipelineInput();
        while (!buffer.put((byte) b)) {
            waitForBuffer();
            checkPipelineInput();
        }
        notifyBuffer();
    }

    @Override
    public synchronized void close() throws IOException {
        closedOut = true;
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
                return closedOut ? -1 : 0;
            }
            int read;
            synchronized (PipelineOutputStream.this) {
                do {
                    read = buffer.get(b, off, len);
                    if (read == 0) {
                        if (closedOut) {
                            return -1;
                        }
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
                    if (closedOut) {
                        return -1;
                    }
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
                        if (closedOut) {
                            return total;
                        }
                        waitForBuffer();
                    } else {
                        total += skipped;
                        notifyBuffer();
                    }
                }
                return total;
            }
        }

        @Override
        public void close() throws IOException {
            closedIn = true;
        }
    }

}
