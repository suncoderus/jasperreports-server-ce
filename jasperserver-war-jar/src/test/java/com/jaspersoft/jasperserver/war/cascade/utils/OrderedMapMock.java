/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.war.cascade.utils;

import org.apache.commons.collections.map.LinkedMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link LinkedMap}
 * which allows to convert Map<Object, List<String>> internally to Map<Object, String[]>.
 * This is useful when we instantiate LinkedMap through spring context
 */
public class OrderedMapMock extends LinkedMap {

    public OrderedMapMock(Map<Object, List<String>> map) {
        Map<Object, String[]> tempMap = new LinkedHashMap<Object, String[]>(map.size());
        for (Map.Entry<Object, List<String>> entry: map.entrySet()) {
            List<String> value = entry.getValue();
            tempMap.put(entry.getKey(), value != null ? value.toArray(new String[] {}) : null);
        }

        this.putAll(tempMap);
    }


}
