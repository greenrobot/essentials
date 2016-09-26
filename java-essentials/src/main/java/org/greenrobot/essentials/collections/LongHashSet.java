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

package org.greenrobot.essentials.collections;

import java.util.Arrays;

/**
 * An minimalistic hash set optimized for long values. The default implementation is not thread-safe, but you can get a
 * synchronized variant using one of the static createSynchronized methods.
 *
 * @author Markus
 */
public class LongHashSet {

    protected static final int DEFAULT_CAPACITY = 16;

    final static class Entry {
        final long key;
        Entry next;

        Entry(long key, Entry next) {
            this.key = key;
            this.next = next;
        }
    }

    /**
     * Creates a synchronized (thread-safe) LongHashSet.
     */
    public static LongHashSet createSynchronized() {
        return new Synchronized(DEFAULT_CAPACITY);
    }

    /**
     * Creates a synchronized (thread-safe) LongHashSet using the given initial capacity.
     */
    public static LongHashSet createSynchronized(int capacity) {
        return new Synchronized(capacity);
    }

    private Entry[] table;
    private int capacity;
    private int threshold;
    private volatile int size;
    private volatile float loadFactor = 1.3f;

    public LongHashSet() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public LongHashSet(int capacity) {
        this.capacity = capacity;
        this.threshold = (int) (capacity * loadFactor + 0.5f);
        this.table = new Entry[capacity];
    }

    public boolean contains(long key) {
        final int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % capacity;

        for (Entry entry = table[index]; entry != null; entry = entry.next) {
            if (entry.key == key) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the given value to the set.
     *
     * @return true if the value was actually new
     */
    public boolean add(long key) {
        final int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % capacity;
        final Entry entryOriginal = table[index];
        for (Entry entry = entryOriginal; entry != null; entry = entry.next) {
            if (entry.key == key) {
                return false;
            }
        }
        table[index] = new Entry(key, entryOriginal);
        size++;
        if (size > threshold) {
            setCapacity(2 * capacity);
        }
        return true;
    }

    /**
     * Removes the given value to the set.
     *
     * @return true if the value was actually removed
     */
    public boolean remove(long key) {
        int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % capacity;
        Entry previous = null;
        Entry entry = table[index];
        while (entry != null) {
            Entry next = entry.next;
            if (entry.key == key) {
                if (previous == null) {
                    table[index] = next;
                } else {
                    previous.next = next;
                }
                size--;
                return true;
            }
            previous = entry;
            entry = next;
        }
        return false;
    }

    /**
     * Returns all keys in no particular order.
     */
    public long[] keys() {
        long[] values = new long[size];
        int idx = 0;
        for (Entry entry : table) {
            while (entry != null) {
                values[idx++] = entry.key;
                entry = entry.next;
            }
        }
        return values;
    }

    public void clear() {
        size = 0;
        Arrays.fill(table, null);
    }

    public int size() {
        return size;
    }

    public void setCapacity(int newCapacity) {
        @SuppressWarnings("unchecked")
        Entry[] newTable = new Entry[newCapacity];
        int length = table.length;
        for (int i = 0; i < length; i++) {
            Entry entry = table[i];
            while (entry != null) {
                long key = entry.key;
                int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % newCapacity;

                Entry originalNext = entry.next;
                entry.next = newTable[index];
                newTable[index] = entry;
                entry = originalNext;
            }
        }
        table = newTable;
        capacity = newCapacity;
        threshold = (int) (newCapacity * loadFactor + 0.5f);
    }

    public void setLoadFactor(float loadFactor) {
        this.loadFactor = loadFactor;
    }

    /** Target load: 0,6 */
    public void reserveRoom(int entryCount) {
        setCapacity((int) (entryCount * loadFactor * 1.3f + 0.5f));
    }

    protected static class Synchronized extends LongHashSet {
        public Synchronized(int capacity) {
            super(capacity);
        }

        @Override
        public synchronized boolean contains(long key) {
            return super.contains(key);
        }

        @Override
        public synchronized boolean add(long key) {
            return super.add(key);
        }

        @Override
        public synchronized boolean remove(long key) {
            return super.remove(key);
        }

        @Override
        public synchronized long[] keys() {
            return super.keys();
        }

        @Override
        public synchronized void clear() {
            super.clear();
        }

        @Override
        public synchronized void setCapacity(int newCapacity) {
            super.setCapacity(newCapacity);
        }

        @Override
        public synchronized void reserveRoom(int entryCount) {
            super.reserveRoom(entryCount);
        }

    }

}
