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

package com.jaspersoft.jasperserver.search.util;

import com.jaspersoft.jasperserver.search.common.ResourceDetails;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;

import java.util.List;
import java.util.Map;

/**
 */
public class CollectionUtils {
    public static String listToDebugString(List list) {
        StringBuilder sb = new StringBuilder("[");
        for(Object o : list) {
            String item = o.toString();

            if(o instanceof ResourceLookup) {
                item = ((ResourceLookup) o).getURIString();
            }
            sb.append(item).append(", ");
        }

        return sb.append("]").toString();
    }

    public static String mapToDebugString(Map map) {
        StringBuilder sb = new StringBuilder("\n{").append("\n");
        for(Object k : map.keySet()) {
            Object v = map.get(k); 

            if (k instanceof Class) {
                sb.append("\t").append(((Class) k).getName()).append(" : ");
            } else  {
                sb.append(k).append(" : ");
            }

            if (v instanceof List) {
                sb.append(listToDebugString((List) v)).append(", ");
            } else  {
                sb.append(v).append(", ");
            }
            sb.append("\n");
        }

        sb.replace(sb.length() - 3, sb.length(), "");
        return sb.append("\n}\n").toString();
    }

}
