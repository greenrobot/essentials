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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Combines a Map with Set values to provide simple way to store multiple values for a key.
 * Like {@link org.greenrobot.essentials.collections.Multimap}, but element values are stored in Sets.
 */
// Top level class to get rid of 3rd generic collection parameter for more convenient usage.
public class MultimapSet<K, V> extends AbstractMultimap<K, V, Set<V>> {
    private final SetType setType;

    public enum SetType {
        /** Aka HashSet */
        REGULAR,

        /** Aka CopyOnWriteArraySet */
        THREAD_SAFE
    }

    public static <K, V> MultimapSet<K, V> create() {
        return create(SetType.REGULAR);
    }

    public static <K, V> MultimapSet<K, V> create(SetType setType) {
        return new MultimapSet<>(new HashMap<K, Set<V>>(), setType);
    }

    protected MultimapSet(Map<K, Set<V>> map, SetType setType) {
        super(map);
        this.setType = setType;
    }

    protected Set<V> createNewCollection() {
        switch (setType) {
            case REGULAR:
                return new HashSet<>();
            case THREAD_SAFE:
                return new CopyOnWriteArraySet<>();
            default:
                throw new IllegalStateException("Unknown set type: " + setType);
        }
    }

}
