/*
 * Copyright (C) 2014-2016 Markus Junginger, greenrobot (http://greenrobot.de)
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
public class ObjectCache<KEY, VALUE> {

    public static enum ReferenceType {
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

    private final Map<KEY, CacheEntry<VALUE>> cache;
    private final ReferenceType referenceType;
    private final boolean isStrongReference;
    private final int maxSize;
    private final long expirationMillis;
    private final boolean isExpiring;

    // No strict multi-threading required for those
    private volatile int putCountSinceEviction;
    private volatile long nextCleanUpTimestamp;

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
        cache = new LinkedHashMap<>();
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

        putCountSinceEviction++;
        if (isExpiring && nextCleanUpTimestamp == 0) {
            nextCleanUpTimestamp = System.currentTimeMillis() + expirationMillis + 1;
        }

        CacheEntry<VALUE> oldEntry;
        synchronized (this) {
            if (cache.size() >= maxSize) {
                evictToTargetSize(maxSize - 1);
            }
            oldEntry = cache.put(key, entry);
        }
        return getValue(oldEntry);
    }

    private VALUE getValue(CacheEntry<VALUE> entry) {
        if (entry != null) {
            return isStrongReference ? entry.referenceStrong : entry.reference.get();
        } else {
            return null;
        }
    }

    /** Stores all entries contained in the given map in the cache. */
    public void putAll(Map<KEY, VALUE> mapDataToPut) {
        int targetSize = maxSize - mapDataToPut.size();
        if (maxSize > 0 && cache.size() > targetSize) {
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
            entry = cache.get(key);
        }
        if (entry != null) {
            if (isExpiring) {
                long age = System.currentTimeMillis() - entry.timeCreated;
                if (age < expirationMillis) {
                    return getValue(entry);
                } else {
                    synchronized (this) {
                        cache.remove(key);
                    }
                    return null;
                }
            } else {
                return getValue(entry);
            }
        } else {
            return null;
        }
    }

    /** Clears all cached entries. */
    public synchronized void clear() {
        cache.clear();
    }

    /**
     * Removes an entry from the cache.
     *
     * @return The removed entry
     */
    public VALUE remove(KEY key) {
        return getValue(cache.remove(key));
    }

    public synchronized void evictToTargetSize(int targetSize) {
        if (targetSize <= 0) {
            cache.clear();
        } else {
            checkCleanUpObsoleteEntries();
            Iterator<KEY> keys = cache.keySet().iterator();
            while (keys.hasNext() && cache.size() > targetSize) {
                keys.next();
                keys.remove();
            }
        }
    }

    void checkCleanUpObsoleteEntries() {
        if (!isStrongReference || isExpiring) {
            long now = System.currentTimeMillis();
            if ((isExpiring && nextCleanUpTimestamp != 0 && now > nextCleanUpTimestamp) ||
                    putCountSinceEviction > maxSize / 2) {
                putCountSinceEviction = 0;
                nextCleanUpTimestamp = 0;

                long timeLimit = isExpiring ? now - expirationMillis : 0;
                Set<Entry<KEY, CacheEntry<VALUE>>> entries = cache.entrySet();
                for (Entry<KEY, CacheEntry<VALUE>> entry : entries) {
                    CacheEntry<VALUE> cacheEntry = entry.getValue();
                    if ((!isStrongReference && cacheEntry.reference == null) || (cacheEntry.timeCreated < timeLimit)) {
                        cache.remove(entry.getKey());
                    }
                }
            }
        }
    }

    public synchronized boolean containsKey(KEY key) {
        return cache.containsKey(key);
    }

    public boolean containsKeyWithValue(KEY key) {
        return get(key) != null;
    }

    public synchronized Set<KEY> keySet() {
        return cache.keySet();
    }

    public synchronized int size() {
        return cache.size();
    }

}
