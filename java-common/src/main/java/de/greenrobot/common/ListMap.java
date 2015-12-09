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

package de.greenrobot.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Combines a Map with List values to provide simple way to store multiple values for a key (multimap).
 * <p>
 * Threading note: if used multithreaded, all direct operations on lists should synchronize the ListMap.
 */
public class ListMap<K, V> extends AbstractMultimap<K, V, List<V>> {

    public ListMap(Map<K, List<V>> map) {
        super(map);
    }

    public static <K, V> ListMap<K, V> createHashMap() {
        return new ListMap<>(new HashMap<K, List<V>>());
    }

    protected List<V> createNewCollection() {
        return new ArrayList<>();
    }

}
