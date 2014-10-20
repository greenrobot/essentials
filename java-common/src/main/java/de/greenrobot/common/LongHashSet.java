package de.greenrobot.common;

import java.util.Arrays;

/**
 * An minimalistic hash set optimized for long values.
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

    public LongHashSet() {
        this(16);
    }

    @SuppressWarnings("unchecked")
    public LongHashSet(int capacity) {
        this.capacity = capacity;
        this.threshold = capacity * 4 / 3;
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
        threshold = newCapacity * 4 / 3;
    }

    /** Target load: 0,6 */
    public void reserveRoom(int entryCount) {
        setCapacity(entryCount * 5 / 3);
    }

}
