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
package com.jaspersoft.jasperserver.war.cascade.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * SessionCacheImpl
 * @author jwhang
 * @version $Id$
 */
public class SessionCacheImpl implements SessionCache {

    private long lastInteraction = System.currentTimeMillis(); //used for marking sessions for cleanup.
    private volatile Map<Class<?>, Map<String, Object>> cacheInfoPool = Collections.synchronizedMap(new HashMap<Class<?>, Map<String, Object>>());

    public long getLastInteraction() {
        return lastInteraction;
    }

    protected void setLastInteraction(long lastInteraction) {
        this.lastInteraction = lastInteraction;
    }

    public <T> void setCacheInfo(Class<T> cachedObjectType, String lookupKey, T cacheInfo){
        setLastInteraction(System.currentTimeMillis());
        getConcreteTypeCache(cachedObjectType).put(lookupKey, cacheInfo);
    }

    public void clear() {
        synchronized (cacheInfoPool) {
            cacheInfoPool.clear();
        }
    }

    // 'SynchronizeOnNonFinalField' - field is volatile
    // 'unchecked' - cast is safe, see setCacheInfo()
    @SuppressWarnings({"SynchronizeOnNonFinalField", "unchecked"})
    protected <T> Map<String, T> getConcreteTypeCache(Class<T> cachedObjectType) {
        if (cacheInfoPool.get(cachedObjectType) == null) {
            synchronized (cacheInfoPool){
                if(cacheInfoPool.get(cachedObjectType) == null){
                    cacheInfoPool.put(cachedObjectType, Collections.synchronizedMap(new HashMap<String, Object>()));
                }
            }
        }

        return (Map<String, T>) cacheInfoPool.get(cachedObjectType);
    }

    public <T> T getCacheInfo(Class<T> cachedObjectType, String lookupKey){
        setLastInteraction(System.currentTimeMillis());
        return getConcreteTypeCache(cachedObjectType).get(lookupKey);
    }
}
