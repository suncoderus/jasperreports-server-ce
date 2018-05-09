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

package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.teiid.cache.Cache;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mchan on 4/6/2017.
 */

public class TeiidCache<K, V> implements Cache<K, V> {

    Ehcache ehcache;
    String cacheName;
    boolean transactional;

    static final Log log = LogFactory.getLog(TeiidCacheFactory.class);

    public Ehcache getEhcache() {
        return ehcache;
    }

    public void setEhcache(Ehcache ehcache) {
        this.ehcache = ehcache;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public V get(K key) {
        Element el = ehcache.get(key);
        if (el != null) {
            if (log.isDebugEnabled()) {
                log.debug("element found for key:" + key.toString());
            }
            return (V) el.getObjectValue();
        }
        return null;
    }

    public V put(K var1, V var2, Long var3) {
        ehcache.put(new Element(var1, var2));
        return var2;
    }

    public V remove(K var1) {
        V val = get(var1);
        if (val != null) {
            ehcache.remove(var1);
            return val;
        } else return null;
    }

    public int size() {
        return ehcache.getSize();
    }

    public void clear() {
        ehcache.removeAll();
    }

    public String getName() {
        return cacheName;
    }

    public Set<K> keySet() {
        return new HashSet<K>(ehcache.getKeys());
    }

    @Override
    public boolean isTransactional() {
        return transactional;
    }

    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    public void shutdown() {
        System.err.println(" -- JasperServer:  TeiidCache " + cacheName + " shutdown called.  This normal shutdown operation. ");
        ehcache.removeAll();

    }
}

