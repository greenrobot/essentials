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

package org.greenrobot.essentials;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An in-memory object cache supporting soft/weak/strong references, maximum size (clearing the entries putted
 * first), and time-based expiration.
 *
 * @author markus
 */
// Decided against providing a value creator/factory because synchronization won't be optimal for general solutions:
// 1. Long lasting creations may block the cache for other threads
// 2. Some creations may be expensive and should not be triggered in parallel (e.g. for the same key)
public class ObjectCache<KEY, VALUE> {

    public enum ReferenceType {
        SOFT, WEAK, STRONG
    }

    static class CacheEntry<V> {
        final Reference<V> reference;
        final V referenceStrong;
        final long timeCreated;

        CacheEntry(Reference<V> reference, V referenceStrong) {
            this.reference = reference;
            this.referenceStrong = referenceStrong;
            timeCreated = System.currentTimeMillis();
        }
    }

    private final Map<KEY, CacheEntry<VALUE>> values;
    private final ReferenceType referenceType;
    private final boolean isStrongReference;
    private final int maxSize;
    private final long expirationMillis;
    private final boolean isExpiring;

    // No strict multi-threading required for those
    private volatile long nextCleanUpTimestamp;
    private volatile int countPutCountSinceEviction;
    private volatile int countPut;
    private volatile int countHit;
    private volatile int countMiss;
    private volatile int countExpired;
    private volatile int countRefCleared;
    private volatile int countEvicted;

    /**
     * Create a cache according to the given configuration.
     *
     * @param referenceType    SOFT is usually a good choice allowing the VM to clear caches when running low on
     *                         memory.
     *                         STRONG may also be preferred, e.g. when the required space is granted.
     * @param maxSize          The maximum number of entries stored by this cache
     * @param expirationMillis
     */
    public ObjectCache(ReferenceType referenceType, int maxSize, long expirationMillis) {
        this.referenceType = referenceType;
        isStrongReference = referenceType == ReferenceType.STRONG;
        this.maxSize = maxSize;
        this.expirationMillis = expirationMillis;
        isExpiring = expirationMillis > 0;
        values = new LinkedHashMap<>();
    }

    /** Stores an new entry in the cache. */
    public VALUE put(KEY key, VALUE object) {
        CacheEntry<VALUE> entry;
        if (referenceType == ReferenceType.WEAK) {
            entry = new CacheEntry<>(new WeakReference<>(object), null);
        } else if (referenceType == ReferenceType.SOFT) {
            entry = new CacheEntry<>(new SoftReference<>(object), null);
        } else {
            entry = new CacheEntry<>(null, object);
        }

        countPutCountSinceEviction++;
        countPut++;
        if (isExpiring && nextCleanUpTimestamp == 0) {
            nextCleanUpTimestamp = System.currentTimeMillis() + expirationMillis + 1;
        }

        CacheEntry<VALUE> oldEntry;
        synchronized (this) {
            if (values.size() >= maxSize) {
                evictToTargetSize(maxSize - 1);
            }
            oldEntry = values.put(key, entry);
        }
        return getValueForRemoved(oldEntry);
    }

    private VALUE getValueForRemoved(CacheEntry<VALUE> entry) {
        if (entry != null) {
            return isStrongReference ? entry.referenceStrong : entry.reference.get();
        } else {
            return null;
        }
    }

    private VALUE getValue(KEY keyForRemoval, CacheEntry<VALUE> entry) {
        if (entry != null) {
            if (isStrongReference) {
                return entry.referenceStrong;
            } else {
                VALUE value = entry.reference.get();
                if (value == null) {
                    countRefCleared++;
                    if (keyForRemoval != null) {
                        synchronized (this) {
                            values.remove(keyForRemoval);
                        }
                    }
                }
                return value;
            }
        } else {
            return null;
        }
    }

    /** Stores all entries contained in the given map in the cache. */
    public void putAll(Map<KEY, VALUE> mapDataToPut) {
        int targetSize = maxSize - mapDataToPut.size();
        if (maxSize > 0 && values.size() > targetSize) {
            evictToTargetSize(targetSize);
        }
        Set<Entry<KEY, VALUE>> entries = mapDataToPut.entrySet();
        for (Entry<KEY, VALUE> entry : entries) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /** Get the cached entry or null if no valid cached entry is found. */
    public VALUE get(KEY key) {
        CacheEntry<VALUE> entry;
        synchronized (this) {
            entry = values.get(key);
        }
        VALUE value;
        if (entry != null) {
            if (isExpiring) {
                long age = System.currentTimeMillis() - entry.timeCreated;
                if (age < expirationMillis) {
                    value = getValue(key, entry);
                } else {
                    countExpired++;
                    synchronized (this) {
                        values.remove(key);
                    }
                    value = null;
                }
            } else {
                value = getValue(key, entry);
            }
        } else {
            value = null;
        }
        if (value != null) {
            countHit++;
        } else {
            countMiss++;
        }
        return value;
    }

    /** Clears all cached entries. */
    public synchronized void clear() {
        values.clear();
    }

    /**
     * Removes an entry from the cache.
     *
     * @return The removed entry
     */
    public VALUE remove(KEY key) {
        return getValueForRemoved(values.remove(key));
    }

    public synchronized void evictToTargetSize(int targetSize) {
        if (targetSize <= 0) {
            values.clear();
        } else {
            checkCleanUpObsoleteEntries();
            Iterator<KEY> keys = values.keySet().iterator();
            while (keys.hasNext() && values.size() > targetSize) {
                countEvicted++;
                keys.next();
                keys.remove();
            }
        }
    }

    void checkCleanUpObsoleteEntries() {
        if (!isStrongReference || isExpiring) {
            if ((isExpiring && nextCleanUpTimestamp != 0 && System.currentTimeMillis() > nextCleanUpTimestamp) ||
                    countPutCountSinceEviction > maxSize / 2) {
                cleanUpObsoleteEntries();
            }
        }
    }

    /**
     * Iterates over all entries to check for obsolete ones (time expired or reference cleared).
     * <p/>
     * Note: Usually you don't need to call this method explicitly, because it is called internally in certain
     * conditions when space has to be reclaimed.
     */
    public synchronized int cleanUpObsoleteEntries() {
        countPutCountSinceEviction = 0;
        nextCleanUpTimestamp = 0;

        int countCleaned = 0;
        long timeLimit = isExpiring ? System.currentTimeMillis() - expirationMillis : 0;
        Iterator<CacheEntry<VALUE>> iterator = values.values().iterator();
        while (iterator.hasNext()) {
            CacheEntry<VALUE> cacheEntry = iterator.next();
            if (!isStrongReference && cacheEntry.reference == null) {
                countRefCleared++;
                countCleaned++;
                iterator.remove();
            } else if (cacheEntry.timeCreated < timeLimit) {
                countExpired++;
                countCleaned++;
                iterator.remove();
            }
        }
        return countCleaned;
    }

    public synchronized boolean containsKey(KEY key) {
        return values.containsKey(key);
    }

    public boolean containsKeyWithValue(KEY key) {
        return get(key) != null;
    }

    public synchronized Set<KEY> keySet() {
        return values.keySet();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public synchronized int size() {
        return values.size();
    }

    public int getCountPut() {
        return countPut;
    }

    public int getCountHit() {
        return countHit;
    }

    public int getCountMiss() {
        return countMiss;
    }

    public int getCountExpired() {
        return countExpired;
    }

    public int getCountRefCleared() {
        return countRefCleared;
    }

    public int getCountEvicted() {
        return countEvicted;
    }

    @Override
    public String toString() {
        return "ObjectCache[maxSize=" + maxSize + ", hits=" + countHit + ", misses=" + countMiss + "]";
    }

    /** Often used in addition to {@link #toString()} to print out states: details why entries were removed. */
    public String getStatsStringRemoved() {
        return "ObjectCache-Removed[expired=" + countExpired + ", refCleared=" + countRefCleared +
                ", evicted=" + countEvicted;
    }
}
