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

/**
 * A circular byte buffer (also called ring buffer) allows putting and and getting bytes in a FIFO way. Typical use
 * cases are (usually concurrent/asynchronous) producers and consumers operating on bytes. This enables building a
 * multi-threaded processing pipeline.
 * <p/>
 * All put&get methods are non-blocking.
 * <p/>
 * This class is thread-safe.
 */
public class CircularByteBuffer {
    private final byte[] buffer;
    private final int capacity;

    private int available;
    private int idxGet;
    private int idxPut;

    public CircularByteBuffer() {
        this(8192);
    }

    public CircularByteBuffer(int capacity) {
        this.capacity = capacity;
        buffer = new byte[this.capacity];
    }

    /**
     * Clears all data from the buffer.
     */
    public synchronized void clear() {
        idxGet = idxPut = available = 0;
    }


    /**
     * Gets a single byte return or -1 if no data is available.
     */
    public synchronized int get() {
        if (available == 0) {
            return -1;
        }
        byte value = buffer[idxGet];
        idxGet = (idxGet + 1) % capacity;
        available--;
        return value;
    }

    /**
     * Gets as many of the requested bytes as available from this buffer.
     *
     * @return number of bytes actually got from this buffer (0 if no bytes are available)
     */
    public int get(byte[] dst) {
        return get(dst, 0, dst.length);
    }

    /**
     * Gets as many of the requested bytes as available from this buffer.
     *
     * @return number of bytes actually got from this buffer (0 if no bytes are available)
     */
    public synchronized int get(byte[] dst, int off, int len) {
        if (available == 0) {
            return 0;
        }

        // limit is last index to read + 1
        int limit = idxGet < idxPut ? idxPut : capacity;
        int count = Math.min(limit - idxGet, len);
        System.arraycopy(buffer, idxGet, dst, off, count);
        idxGet += count;

        if (idxGet == capacity) {
            // Array end reached, check if we have more
            int count2 = Math.min(len - count, idxPut);
            if (count2 > 0) {
                System.arraycopy(buffer, 0, dst, off + count, count2);
                idxGet = count2;
                count += count2;
            } else {
                idxGet = 0;
            }
        }
        available -= count;
        return count;
    }


    /**
     * Puts a single byte if the buffer is not yet full.
     *
     * @return true if the byte was put, or false if the buffer is full
     */
    public synchronized boolean put(byte value) {
        if (available == capacity) {
            return false;
        }
        buffer[idxPut] = value;
        idxPut = (idxPut + 1) % capacity;
        available++;
        return true;
    }

    /**
     * Puts as many of the given bytes as possible into this buffer.
     *
     * @return number of bytes actually put into this buffer (0 if the buffer is full)
     */
    public int put(byte[] src) {
        return put(src, 0, src.length);
    }

    /**
     * Puts as many of the given bytes as possible into this buffer.
     *
     * @return number of bytes actually put into this buffer (0 if the buffer is full)
     */
    public synchronized int put(byte[] src, int off, int len) {
        if (available == capacity) {
            return 0;
        }

        // limit is last index to put + 1
        int limit = idxPut < idxGet ? idxGet : capacity;
        int count = Math.min(limit - idxPut, len);
        System.arraycopy(src, off, buffer, idxPut, count);
        idxPut += count;

        if (idxPut == capacity) {
            // Array end reached, check if we have more
            int count2 = Math.min(len - count, idxGet);
            if (count2 > 0) {
                System.arraycopy(src, off + count, buffer, 0, count2);
                idxPut = count2;
                count += count2;
            } else {
                idxPut = 0;
            }
        }
        available += count;
        return count;
    }

    /**
     * Return the first byte a <b>get</b> would return or -1 if no data is available.
     */
    public synchronized int peek() {
        return available > 0 ? buffer[idxGet] : -1;
    }

    /**
     * Skips the given count of bytes, but at most the currently available count.
     *
     * @return number of bytes actually skipped from this buffer (0 if no bytes are available)
     */
    public synchronized int skip(int count) {
        if (count > available) {
            count = available;
        }
        idxGet = (idxGet + count) % capacity;
        available -= count;
        return count;
    }


    /**
     * The capacity (size) is the maximum of bytes that can be stored inside this buffer.
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Returns the number of bytes available and can be get without additional puts.
     */
    public synchronized int available() {
        return available;
    }

    /**
     * Returns the number of free bytes available that can still be put without additional gets.
     */
    public synchronized int free() {
        return capacity - available;
    }
}
