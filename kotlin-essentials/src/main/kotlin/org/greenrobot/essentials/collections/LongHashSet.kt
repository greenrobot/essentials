/*
 * Copyright (C) 2014-2020 Markus Junginger, greenrobot (http://greenrobot.org)
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
package org.greenrobot.essentials.collections

import java.util.*

/**
 * An minimalistic hash set optimized for long values. The default implementation is not thread-safe, but you can get a
 * synchronized variant using one of the static createSynchronized methods.
 *
 * @author Markus
 */
open class LongHashSet @JvmOverloads constructor(private var capacity: Int = DEFAULT_CAPACITY) {
    internal class Entry(val key: Long, var next: Entry?)

    private var table: Array<Entry?>
    private var threshold: Int

    @Volatile
    private var size = 0

    @Volatile
    private var loadFactor = 1.3f
    open operator fun contains(key: Long): Boolean {
        val index = ((key ushr 32).toInt() xor key.toInt() and 0x7fffffff) % capacity
        var entry = table[index]
        while (entry != null) {
            if (entry.key == key) {
                return true
            }
            entry = entry.next
        }
        return false
    }

    /**
     * Adds the given value to the set.
     *
     * @return true if the value was actually new
     */
    open fun add(key: Long): Boolean {
        val index = ((key ushr 32).toInt() xor key.toInt() and 0x7fffffff) % capacity
        val entryOriginal = table[index]
        var entry = entryOriginal
        while (entry != null) {
            if (entry.key == key) {
                return false
            }
            entry = entry.next
        }
        table[index] = Entry(key, entryOriginal)
        size++
        if (size > threshold) {
            setCapacity(2 * capacity)
        }
        return true
    }

    /**
     * Removes the given value to the set.
     *
     * @return true if the value was actually removed
     */
    open fun remove(key: Long): Boolean {
        val index = ((key ushr 32).toInt() xor key.toInt() and 0x7fffffff) % capacity
        var previous: Entry? = null
        var entry = table[index]
        while (entry != null) {
            val next = entry.next!!
            if (entry.key == key) {
                if (previous == null) {
                    table[index] = next
                } else {
                    previous.next = next
                }
                size--
                return true
            }
            previous = entry
            entry = next
        }
        return false
    }

    /**
     * Returns all keys in no particular order.
     */
    open fun keys(): LongArray {
        val values = LongArray(size)
        var idx = 0
        for (tableEntry in table) {
            var entry = tableEntry
            while (entry != null) {
                values[idx++] = entry.key
                entry = entry.next
            }
        }
        return values
    }

    open fun clear() {
        size = 0
        Arrays.fill(table, null)
    }

    fun size(): Int {
        return size
    }

    open fun setCapacity(newCapacity: Int) {
        val newTable = arrayOfNulls<Entry>(newCapacity)
        for (value in table) {
            var entry = value
            while (entry != null) {
                val key = entry.key
                val index = ((key ushr 32).toInt() xor key.toInt() and 0x7fffffff) % newCapacity
                val originalNext = entry.next!!
                entry.next = newTable[index]
                newTable[index] = entry
                entry = originalNext
            }
        }
        table = newTable
        capacity = newCapacity
        threshold = (newCapacity * loadFactor + 0.5f).toInt()
    }

    fun setLoadFactor(loadFactor: Float) {
        this.loadFactor = loadFactor
    }

    /** Target load: 0,6  */
    open fun reserveRoom(entryCount: Int) {
        setCapacity((entryCount * loadFactor * 1.3f + 0.5f).toInt())
    }

    protected class Synchronized(capacity: Int) : LongHashSet(capacity) {
        @kotlin.jvm.Synchronized
        override fun contains(key: Long): Boolean {
            return super.contains(key)
        }

        @kotlin.jvm.Synchronized
        override fun add(key: Long): Boolean {
            return super.add(key)
        }

        @kotlin.jvm.Synchronized
        override fun remove(key: Long): Boolean {
            return super.remove(key)
        }

        @kotlin.jvm.Synchronized
        override fun keys(): LongArray {
            return super.keys()
        }

        @kotlin.jvm.Synchronized
        override fun clear() {
            super.clear()
        }

        @kotlin.jvm.Synchronized
        override fun setCapacity(newCapacity: Int) {
            super.setCapacity(newCapacity)
        }

        @kotlin.jvm.Synchronized
        override fun reserveRoom(entryCount: Int) {
            super.reserveRoom(entryCount)
        }
    }

    companion object {
        protected const val DEFAULT_CAPACITY = 16

        /**
         * Creates a synchronized (thread-safe) LongHashSet.
         */
        fun createSynchronized(): LongHashSet {
            return Synchronized(DEFAULT_CAPACITY)
        }

        /**
         * Creates a synchronized (thread-safe) LongHashSet using the given initial capacity.
         */
        fun createSynchronized(capacity: Int): LongHashSet {
            return Synchronized(capacity)
        }
    }

    init {
        threshold = (capacity * loadFactor + 0.5f).toInt()
        table = arrayOfNulls(capacity)
    }
}