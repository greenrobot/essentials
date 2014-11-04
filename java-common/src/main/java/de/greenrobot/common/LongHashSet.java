/*
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
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

package de.greenrobot.common;

import java.util.Arrays;

/**
 * An minimalistic hash set optimized for long values. Not thread-safe.
 *
 * @author Markus
 */
public final class LongHashSet {
    final static class Entry {
        final long key;
        Entry next;

        Entry(long key, Entry next) {
            this.key = key;
            this.next = next;
        }
    }

    private Entry[] table;
    private int capacity;
    private int threshold;
    private int size;
    private float loadFactor = 1.3f;

    public LongHashSet() {
        this(16);
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

    public float getLoadFactor() {
        return loadFactor;
    }

    public void setLoadFactor(float loadFactor) {
        this.loadFactor = loadFactor;
    }

    /** Target load: 0,6 */
    public void reserveRoom(int entryCount) {
        setCapacity((int) (entryCount * loadFactor * 1.3f + 0.5f));
    }

}
