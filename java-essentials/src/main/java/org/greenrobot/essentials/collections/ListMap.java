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

package org.greenrobot.essentials.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Combines a Map with List values to provide simple way to store multiple values for a key (multimap).
 */
public class ListMap<K, V> extends AbstractMultimap<K, V, List<V>> {

    private final boolean threadSafeLists;

    public ListMap() {
        this(new HashMap<K, List<V>>(), false);
    }

    public ListMap(boolean threadSafeLists) {
        this(new HashMap<K, List<V>>(), threadSafeLists);
    }

    public ListMap(Map<K, List<V>> map, boolean threadSafeLists) {
        super(map);
        this.threadSafeLists = threadSafeLists;
    }

    protected List<V> createNewCollection() {
        return threadSafeLists ? new CopyOnWriteArrayList<V>() : new ArrayList<V>();
    }

}
