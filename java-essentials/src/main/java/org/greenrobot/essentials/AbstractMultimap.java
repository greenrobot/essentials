/*
 * Copyright (C) 2014-2015 Markus Junginger, greenrobot (http://greenrobot.de)
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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Combines a Map with List values to provide simple way to store multiple values for a key (multimap).
 * <p>
 * Threading note: methods operating on elements are synchronized because they are not atomic.
 */
public abstract class AbstractMultimap<K, V, C extends Collection<V>> implements Map<K, C> {
    protected Map<K, C> map;


    public AbstractMultimap(Map<K, C> map) {
        this.map = map;
    }

    abstract protected C createNewCollection();

    @Override
    public void putAll(Map<? extends K, ? extends C> m) {
        map.putAll(m);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public C get(Object key) {
        return map.get(key);
    }


    @Override
    public C remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<C> values() {
        return map.values();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    public synchronized void putElement(K key, V value) {
        C collection = map.get(key);
        if (collection == null) {
            collection = createNewCollection();
            map.put(key, collection);
        }
        collection.add(value);
    }

    @Override
    public C put(K key, C value) {
        return map.put(key, value);
    }

    @Override
    public Set<Entry<K, C>> entrySet() {
        return map.entrySet();
    }

    /** @return true if the collection was changed. */
    public synchronized boolean putElements(K key, Collection<V> values) {
        C collection = map.get(key);
        if (collection == null) {
            collection = createNewCollection();
            map.put(key, collection);
        }
        return collection.addAll(values);
    }

    /** @return true if the given element was removed. */
    public synchronized boolean removeElement(K key, V value) {
        C collection = map.get(key);
        if (collection == null) {
            return false;
        } else {
            boolean removed = collection.remove(value);
            if (collection.isEmpty()) {
                map.remove(key);
            }
            return removed;
        }
    }

    public synchronized boolean containsElement(K key, V value) {
        C collection = map.get(key);
        if (collection == null) {
            return false;
        } else {
            return collection.contains(value);
        }
    }

    public synchronized boolean containsElement(V value) {
        for (C collection : map.values()) {
            if (collection.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public synchronized C valuesElements() {
        C all = createNewCollection();
        for (C collection : map.values()) {
            all.addAll(collection);
        }
        return all;
    }

}
