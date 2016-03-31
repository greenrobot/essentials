package org.greenrobot.essentials.javaperf;

import org.greenrobot.essentials.io.PipelineOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.CountDownLatch;

public class PipelineStreamBenchmark {
    static int STREAM_LENGTH = 1 * 1024 * 1024; // 1 MB
    static final int[] SIZES = {8, 64, 128, 512, 1024, 2048, 4096, 8192};

    // this is only for development purposes or to run tests separately. For automated benchmarking use gradle
    public static void main(String[] args) {
        BenchmarkRunner.runWallTime(new LibImpl(), 100, 10);
    }

    public static void transfer(final InputStream inputStream, final OutputStream outputStream, final int amount) {
        final CountDownLatch latch = new CountDownLatch(1);

        new Thread() {
            @Override
            public void run() {
                try {
                    final int[] sizes = SIZES;
                    final int sizesLength = sizes.length;
                    final byte[] buffer = new byte[sizes[sizesLength - 1]];

                    int sizeIndex = 0;
                    for (int position = 0; position < amount; ) {
                        final int nextLength = Math.min(sizes[sizeIndex], STREAM_LENGTH - position);
                        outputStream.write(buffer, 0, nextLength);
                        position += nextLength;
                        sizeIndex = (sizeIndex  + 1) % sizes.length;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    final byte[] buffer = new byte[SIZES[SIZES.length - 1]];
                    while (inputStream.read(buffer, 0, buffer.length) != -1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }
        }.start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private PipelineStreamBenchmark() {
    }

    public static class LibImpl implements Runnable {
        private final int streamLength;

        public LibImpl(int streamLength) {
            this.streamLength = streamLength;
        }

        public LibImpl() {
            this(STREAM_LENGTH);
        }

        @Override
        public void run() {
            final PipelineOutputStream stream = new PipelineOutputStream();
            transfer(stream.getInputStream(), stream, streamLength);
        }

        @Override
        public String toString() {
            return "PipelineStream (" + (streamLength / 1024) + "KB)/Lib";
        }
    }

    public static class StdImpl implements Runnable {

        private final int streamLength;

        public StdImpl(int streamLength) {
            this.streamLength = streamLength;
        }

        public StdImpl() {
            this(STREAM_LENGTH);
        }

        @Override
        public void run() {
            try {
                final PipedInputStream istream = new PipedInputStream();
                final PipedOutputStream ostream = new PipedOutputStream(istream);
                transfer(istream, ostream, streamLength);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString() {
            return "PipelineStream (" + (streamLength / 1024) + "KB)/Std";
        }
    }
}
