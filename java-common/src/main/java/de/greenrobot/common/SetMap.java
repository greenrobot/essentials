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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Combines a Map with Set values to provide simple way to store multiple values for a key (multimap).
 */
public class SetMap<K, V> extends AbstractMultimap<K, V, Set<V>> {

    private final boolean threadSafeSets;

    public SetMap() {
        this(new HashMap<K, Set<V>>(), false);
    }

    public SetMap(Map<K, Set<V>> map, boolean threadSafeSets) {
        super(map);
        this.threadSafeSets = threadSafeSets;
    }

    protected Set<V> createNewCollection() {
        return threadSafeSets ? new CopyOnWriteArraySet<V>() : new HashSet<V>();
    }

}
