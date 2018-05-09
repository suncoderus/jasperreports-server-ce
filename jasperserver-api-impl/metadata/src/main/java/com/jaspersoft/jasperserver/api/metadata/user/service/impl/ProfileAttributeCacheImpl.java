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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeCache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.impl.DefaultSizeOfEngine;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Created with IntelliJ IDEA.
 * User: nthapa
 * Date: 3/12/15
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProfileAttributeCacheImpl implements ProfileAttributeCache {

    static final Log log = LogFactory.getLog(ProfileAttributeCacheImpl.class);

    public Ehcache getAttributeCache() {
        return attributeCache;
    }

    public void setAttributeCache(Ehcache attributeCache) {
        this.attributeCache = attributeCache;
    }

    private Ehcache attributeCache;




    @Override
    public void addItem(Object key, Object value) {
        attributeCache.put(new Element(key, value));
    }

    @Override
    public Object getItem(Object key) {

       Element el= attributeCache.get(key);
        if(el!=null)
        {
            if(log.isDebugEnabled())
            {
                log.debug("element found for key:"+ key.toString());
            }
            return el.getObjectValue();
        }
        return null;
    }

    @Override
    public void removeItem(Object key) {
        attributeCache.remove(key);
    }

    @Override
    public void clearAll() {
        attributeCache.removeAll();
    }

    public void shutdown() {
        System.err.println(" -- JasperServer:  ProfileAttributeCacheImpl shutdown called.  This normal shutdown operation. ");
        attributeCache.removeAll();

    }
}
