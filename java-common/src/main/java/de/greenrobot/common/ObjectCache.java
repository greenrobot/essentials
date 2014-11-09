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
 * A simple in-memory object cache based on a ConcurrentHashMap and SoftReferences.
 * 
 * @author markus
 */
public abstract class ObjectCache<KEY, T> {

    private final Map<KEY, Reference<T>> cache =  new ConcurrentHashMap<>();

    /** Weak references are a bad cache but the work with external allocations like bitmaps. */
    public static <KEY, T> ObjectCache<KEY, T> createUsingWeakReferences() {
        return new WeakReferenceObjectCache<>();
    }
    
    /** Never use this for external allocations like bitmaps: they are not GCed (tested under Android 2.2)! */
    public static <KEY, T> ObjectCache<KEY, T> createUsingSoftReferences() {
        return new SoftReferenceObjectCache<>();
    }

    protected abstract Reference<T> getReferenceObject(T object);
    
    /** Stores an new entry in the cache. */
    public T put(KEY key, T object) {
        Reference<T> ref = getReferenceObject(object);
        Reference<T> oldRef = cache.put(key, ref);
        return oldRef != null ? oldRef.get() : null;
    }
    
    /** Stores all entries contained in the given map in the cache. */
    public void putAll(Map<KEY, T> mapDataToPut) {
        Set<Entry<KEY, T>> entries = mapDataToPut.entrySet();
        for (Entry<KEY, T> entry : entries) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    /** Get the cached entry or null if no valid cached entry is found. */
    public T get(KEY key) {
        Reference<T> ref = cache.get(key);
        return ref != null ? ref.get() : null;
    }
    
    /** Clears all cached entries. */
    public void clear() {
        cache.clear();
    }
    
    /** Removes an entry from the cache. @return The removed entry */
    public T remove(KEY key) {
        Reference<T> oldRef = cache.remove(key);
        return oldRef != null ? oldRef.get() : null;
    }
    
    /** Removes zombie entries (entries whose reference was set to null). */
    public void cleanUp() {
        Set<KEY> keySet = cache.keySet();
        for (KEY key : keySet) {
            Reference<T> ref = cache.get(key);
            if (ref != null && ref.get() == null) {
                cache.remove(key);
            }
        }
    }
    
    /** Returns true if an entry was found with no value: mainly for testing purposes. */
    public boolean isValueExpired(KEY key) {
        Reference<T> ref = cache.get(key);
        return ref != null && ref.get() == null;
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

    private static class WeakReferenceObjectCache<KEY, T> extends ObjectCache<KEY, T> {

       @Override
       protected Reference<T> getReferenceObject(T object) {
          return new WeakReference<>(object);
       }

    }

    private static class SoftReferenceObjectCache<KEY, T> extends ObjectCache<KEY, T> {

       @Override
       protected Reference<T> getReferenceObject(T object) {
          return new SoftReference<>(object);
       }

    }

}
