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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple in-memory object "cache" based on a ConcurrentHashMap and soft/weak/strong references.
 * <p>
 * In contrast to Java's WeakHashMap, it does not weakly reference keys but values.
 * <p>
 *
 * @author markus
 */
public class ObjectCache<KEY, VALUE> {

    public static enum ReferenceType {SOFT, WEAK, STRONG}

    static class CacheEntry<V> {
        final Reference<V> reference;
        final V referenceStrong;
        final long timeCreated;
        volatile long timeAccessed;

        CacheEntry(Reference<V> reference, V referenceStrong) {
            this.reference = reference;
            this.referenceStrong = referenceStrong;
            timeCreated = timeAccessed = System.currentTimeMillis();
        }
    }

    private final ReferenceType referenceType;
    private final Map<KEY, CacheEntry<VALUE>> cache;

    private int maxSize;
    private long expirationMillis;
    private boolean lru;

    public ObjectCache(ReferenceType referenceType) {
        this.referenceType = referenceType;
        cache = new ConcurrentHashMap<>();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSizeNonLru(int maxSize) {
        this.maxSize = maxSize;
        lru = false;
    }

    public void setMaxSizeLru(int maxSize) {
        this.maxSize = maxSize;
        lru = true;
    }

    public long getExpirationMillis() {
        return expirationMillis;
    }

    public void setExpirationMillis(long expirationMillis) {
        this.expirationMillis = expirationMillis;
    }

    public boolean isLru() {
        return lru;
    }

    /** Stores an new entry in the cache. */
    public VALUE put(KEY key, VALUE object) {
        if (maxSize > 0 && cache.size() > maxSize) {
            makeRoom();
        }
        CacheEntry<VALUE> entry;
        if (referenceType == ReferenceType.WEAK) {
            entry = new CacheEntry<>(new WeakReference<>(object), null);
        } else if (referenceType == ReferenceType.SOFT) {
            entry = new CacheEntry<>(new SoftReference<>(object), null);
        } else {
            entry = new CacheEntry<>(null, object);
        }
        CacheEntry<VALUE> oldEntry = cache.put(key, entry);
        return getValue(oldEntry);
    }

    private VALUE getValue(CacheEntry<VALUE> entry) {
        if (entry != null) {
            return referenceType == ReferenceType.STRONG ? entry.referenceStrong : entry.reference.get();
        } else {
            return null;
        }
    }

    /** Stores all entries contained in the given map in the cache. */
    public void putAll(Map<KEY, VALUE> mapDataToPut) {
        Set<Entry<KEY, VALUE>> entries = mapDataToPut.entrySet();
        for (Entry<KEY, VALUE> entry : entries) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /** Get the cached entry or null if no valid cached entry is found. */
    public VALUE get(KEY key) {
        CacheEntry<VALUE> entry = cache.get(key);
        if (entry != null) {
            if (expirationMillis > 0) {
                long age = System.currentTimeMillis() - entry.timeCreated;
                if (age < expirationMillis) {
                    return getValue(entry);
                } else {
                    cache.remove(key);
                    return null;
                }
            }
            return getValue(entry);
        } else {
            return null;
        }
    }

    /** Clears all cached entries. */
    public void clear() {
        cache.clear();
    }

    /** Removes an entry from the cache. @return The removed entry */
    public VALUE remove(KEY key) {
        return getValue(cache.remove(key));
    }

    public void makeRoom() {
        long now = System.currentTimeMillis();
        Set<Entry<KEY, CacheEntry<VALUE>>> entries = cache.entrySet();
        for (Entry<KEY, CacheEntry<VALUE>> entry : entries) {
            CacheEntry<VALUE> cacheEntry = entry.getValue();
            if ((referenceType != ReferenceType.STRONG && cacheEntry.reference == null) ||
                    (expirationMillis > 0 && now - cacheEntry.timeCreated > expirationMillis)) {
                cache.remove(entry.getKey());
            }
        }

    }

    public boolean containsKey(KEY key) {
        return cache.containsKey(key);
    }

    public boolean containsKeyWithValue(KEY key) {
        return get(key) != null;
    }

    public Set<KEY> keySet() {
        return cache.keySet();
    }

    public int size() {
        return cache.size();
    }

}
