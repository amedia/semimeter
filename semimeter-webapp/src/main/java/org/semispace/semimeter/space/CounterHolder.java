/*
 * Copyright 2009 Erlend Nossum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.semispace.semimeter.space;

import org.semispace.semimeter.bean.Item;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * Holder for counted elements. A premiss for using this class
 * is that it is accessed in a synchronized manner.
 * It is also presumed that this element has a second in resolution.
 */
public class CounterHolder {
    private Map<String, Item> items = new HashMap<String, Item>();
    public static final int RESOLUTION_MS = 1000;

    public void count( String path ) {
        Item item = items.get(path);
        if ( item == null ) {
            item = new Item();
            long when = System.currentTimeMillis();
            // We have a resolution of a second, so there is no need to retain the fraction
            when -= (when % RESOLUTION_MS);
            item.setWhen(when);
            item.setPath(path);
            items.put(path, item);
        }
        item.increment();
    }

    public Collection<Item> retrieveItems() {
        return items.values();
    }
    public int size() {
        return items.size();
    }
}
