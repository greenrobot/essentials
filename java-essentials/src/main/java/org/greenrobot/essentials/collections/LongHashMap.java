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
 * An minimalistic hash map optimized for long keys. The default implementation is not thread-safe, but you can get a
 * synchronized variant using one of the static createSynchronized methods.
 *
 * @param <T> The value class to store.
 * @author Markus
 */
public class LongHashMap<T> {
    protected static final int DEFAULT_CAPACITY = 16;

    public final static class Entry<T> {
        public final long key;
        public T value;
        Entry<T> next;

        Entry(long key, T value, Entry<T> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /**
     * Creates a synchronized (thread-safe) LongHashSet.
     */
    public static <T> LongHashMap<T> createSynchronized() {
        return new Synchronized<>(DEFAULT_CAPACITY);
    }

    /**
     * Creates a synchronized (thread-safe) LongHashSet using the given initial capacity.
     */
    public static <T> LongHashMap<T> createSynchronized(int capacity) {
        return new Synchronized<>(capacity);
    }

    private Entry<T>[] table;
    private int capacity;
    private int threshold;
    private volatile int size;

    public LongHashMap() {
        this(16);
    }

    @SuppressWarnings("unchecked")
    public LongHashMap(int capacity) {
        this.capacity = capacity;
        this.threshold = capacity * 4 / 3;
        this.table = new Entry[capacity];
    }

    public boolean containsKey(long key) {
        final int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % capacity;

        for (Entry<T> entry = table[index]; entry != null; entry = entry.next) {
            if (entry.key == key) {
                return true;
            }
        }
        return false;
    }

    public T get(long key) {
        final int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % capacity;
        for (Entry<T> entry = table[index]; entry != null; entry = entry.next) {
            if (entry.key == key) {
                return entry.value;
            }
        }
        return null;
    }

    public T put(long key, T value) {
        final int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % capacity;
        final Entry<T> entryOriginal = table[index];
        for (Entry<T> entry = entryOriginal; entry != null; entry = entry.next) {
            if (entry.key == key) {
                T oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
        }
        table[index] = new Entry<>(key, value, entryOriginal);
        size++;
        if (size > threshold) {
            setCapacity(2 * capacity);
        }
        return null;
    }

    public T remove(long key) {
        int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % capacity;
        Entry<T> previous = null;
        Entry<T> entry = table[index];
        while (entry != null) {
            Entry<T> next = entry.next;
            if (entry.key == key) {
                if (previous == null) {
                    table[index] = next;
                } else {
                    previous.next = next;
                }
                size--;
                return entry.value;
            }
            previous = entry;
            entry = next;
        }
        return null;
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

    /**
     * Returns all entries in no particular order.
     */
    public Entry<T>[] entries() {
        @SuppressWarnings("unchecked")
        Entry<T>[] entries = new Entry[size];
        int idx = 0;
        for (Entry entry : table) {
            while (entry != null) {
                entries[idx++] = entry;
                entry = entry.next;
            }
        }
        return entries;
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
        Entry<T>[] newTable = new Entry[newCapacity];
        int length = table.length;
        for (int i = 0; i < length; i++) {
            Entry<T> entry = table[i];
            while (entry != null) {
                long key = entry.key;
                int index = ((((int) (key >>> 32)) ^ ((int) (key))) & 0x7fffffff) % newCapacity;

                Entry<T> originalNext = entry.next;
                entry.next = newTable[index];
                newTable[index] = entry;
                entry = originalNext;
            }
        }
        table = newTable;
        capacity = newCapacity;
        threshold = newCapacity * 4 / 3;
    }

    /** Target load: 0,6 */
    public void reserveRoom(int entryCount) {
        setCapacity(entryCount * 5 / 3);
    }

    protected static class Synchronized<T> extends LongHashMap<T> {
        public Synchronized(int capacity) {
            super(capacity);
        }

        @Override
        public synchronized boolean containsKey(long key) {
            return super.containsKey(key);
        }

        @Override
        public synchronized T get(long key) {
            return super.get(key);
        }

        @Override
        public synchronized T put(long key, T value) {
            return super.put(key, value);
        }

        @Override
        public synchronized T remove(long key) {
            return super.remove(key);
        }

        @Override
        public synchronized long[] keys() {
            return super.keys();
        }

        @Override
        public synchronized Entry<T>[] entries() {
            return super.entries();
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
