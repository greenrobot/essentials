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

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class PipelineOutputStreamTest {
    static final boolean LONG_TEST = false;

    @Test
    public void testBasics() throws IOException {
        PipelineOutputStream out = new PipelineOutputStream();
        InputStream in = out.getInputStream();
        assertEquals(0, in.available());

        byte[] bytes = createBytes(16);
        out.write(bytes);
        assertEquals(16, in.available());
        assertEquals(1, in.read());
        assertEquals(2, in.read());
        assertEquals(14, in.available());

        byte[] buffer = new byte[16];
        assertEquals(4, in.read(buffer, 1, 4));
        assertEquals(10, in.available());
        assertEquals(3, buffer[1]);
        assertEquals(6, buffer[4]);

        assertEquals(10, in.read(buffer));
        assertEquals(0, in.available());
        assertEquals(7, buffer[0]);
        assertEquals(16, buffer[9]);
        assertEquals(0, buffer[10]);
    }

    @Test
    public void testSkip() throws IOException {
        PipelineOutputStream out = new PipelineOutputStream();
        InputStream in = out.getInputStream();
        byte[] bytes = createBytes(16);
        out.write(bytes);
        in.skip(4);
        assertEquals(12, in.available());
        assertEquals(5, in.read());
    }

    @Test
    public void testCloseOut() throws IOException {
        PipelineOutputStream out = new PipelineOutputStream();
        InputStream in = out.getInputStream();
        byte[] bytes = createBytes(16);
        out.write(bytes);
        out.close();
        assertEquals(16, in.available());
        assertEquals(16, in.read(new byte[16]));
        assertEquals(-1, in.read());
        assertEquals(-1, in.read(new byte[16]));
    }

    @Test
    public void testCloseOutWakesReader() throws IOException, InterruptedException {
        PipelineOutputStream out = new PipelineOutputStream();
        final InputStream in = out.getInputStream();
        final CountDownLatch started = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);

        Thread threadReader = new Thread() {
            @Override
            public void run() {
                started.countDown();
                try {
                    in.read(new byte[16]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                done.countDown();
            }
        };
        threadReader.start();

        assertTrue(started.await(1, TimeUnit.SECONDS));
        assertFalse(done.await(2, TimeUnit.MICROSECONDS));
        out.close();
        assertTrue(done.await(1, TimeUnit.SECONDS));
    }

    @Test(expected = IOException.class)
    public void testCloseIn_writeByteArray() throws IOException {
        PipelineOutputStream out = new PipelineOutputStream();
        out.getInputStream().close();
        out.write(new byte[16]);
    }

    @Test(expected = IOException.class)
    public void testCloseIn_writeSingleByte() throws IOException {
        PipelineOutputStream out = new PipelineOutputStream();
        out.getInputStream().close();
        out.write(42);
    }

    @Test
    public void testTwoThreads() throws IOException, InterruptedException {
        final PipelineOutputStream out = new PipelineOutputStream(271);
        InputStream in = out.getInputStream();
        int blockLen = 257;
        final byte[] bytes = createBytes(blockLen);
        final int runs = LONG_TEST ? 25000 : 1000; //

        Thread threadWriter = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    try {
                        out.write(bytes);
                        simulateWork();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        };
        threadWriter.start();

        byte[] readBuffer = new byte[211];
        int totalRead = 0;

        while (totalRead < blockLen * runs) {
            int read = in.read(readBuffer);
            assertTrue(read > 0);
            //System.out.println("> " + totalRead + "+" + read);
            for (int i = 0; i < read; i++) {
                assertEquals(bytes[(totalRead + i) % blockLen], readBuffer[i]);
            }
            totalRead += read;
            simulateWork();
        }
        assertEquals(0, in.available());
    }

    private void simulateWork() throws InterruptedException {
        double random = Math.random();
        if (random > 0.95) {
            Thread.sleep(0, (int) (Math.random() * 999999));
        } else if (random > 0.7) {
            Thread.yield();
        }
    }

    private byte[] createBytes(int len) {
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (i + 1);
        }
        return bytes;
    }
}