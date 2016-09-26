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

package org.greenrobot.essentials.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Combines a Map with List values to provide simple way to store multiple values for a key.
 */
// Top level class to get rid of 3rd generic collection parameter for more convenient usage.
public class Multimap<K, V> extends AbstractMultimap<K, V, List<V>> {
    private final ListType listType;

    public enum ListType {
        /** Aka ArrayList */
        REGULAR,

        /** Aka CopyOnWriteArrayList */
        THREAD_SAFE,

        /** Aka LinkedList */
        LINKED
    }

    public static <K, V> Multimap<K, V> create() {
        return create(ListType.REGULAR);
    }

    public static <K, V> Multimap<K, V> create(ListType listType) {
        return new Multimap<>(new HashMap<K, List<V>>(), listType);
    }

    protected Multimap(Map<K, List<V>> map, ListType listType) {
        super(map);
        this.listType = listType;
        if (listType == null) {
            throw new IllegalArgumentException("List type may not be null");
        }
    }

    protected List<V> createNewCollection() {
        switch (listType) {
            case REGULAR:
                return new ArrayList<>();
            case THREAD_SAFE:
                return new CopyOnWriteArrayList<>();
            case LINKED:
                return new LinkedList<>();
            default:
                throw new IllegalStateException("Unknown list type: " + listType);
        }
    }

}
