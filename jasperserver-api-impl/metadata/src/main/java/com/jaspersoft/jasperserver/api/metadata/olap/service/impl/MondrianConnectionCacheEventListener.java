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
package com.jaspersoft.jasperserver.api.metadata.olap.service.impl;

import com.jaspersoft.jasperserver.api.metadata.olap.service.MondrianConnectionSchemaParameters;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapManagementService;
import java.util.Properties;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

/**
 * Manage Mondrian Connection cache updates of the cache on the local server.
 *
 *
 * @author swood
 */
public class MondrianConnectionCacheEventListener implements CacheEventListener {

    private static final Log log = LogFactory.getLog(MondrianConnectionCacheEventListener.class);

    OlapManagementService olapManagementService;

    public MondrianConnectionCacheEventListener(ApplicationContext ctx, Properties properties) {

        olapManagementService = (OlapManagementService) ctx.getBean("olapManagementService");
    }

    public void dispose() {
        // don't care
    }

    /**
     * element evicted due to size limit of cache. we are not interested
     *
     * @param cache
     * @param element
     */
    public void notifyElementEvicted(Ehcache cache, Element element) {
        // exceeded cache size
    }

    /**
     * Schema element expired. If we are watching it, remove it from the Schema pool
     * and flush the Mondrian cache related to it.
     *
     * @param cache
     * @param element
     */
    public void notifyElementExpired(Ehcache cache, Element element) {
        // expired from not being used
        // clear the cache of old Schemas related to the element
        flushElement(element);
    }

    /**
     * New schema put in. We are not interested
     *
     * @param cache
     * @param element
     * @throws CacheException
     */
    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
    }

    /**
     * Schema removed. If we are watching it, remove it from the Schema pool
     * and flush the Mondrian cache related to it.
     *
     * @param cache
     * @param element URI string -> OlapClientConnection
     * @throws CacheException
     */
    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
        flushElement(element);
    }

    private void flushElement(Element element) {
        if (!(element.getValue() instanceof MondrianConnectionSchemaParameters)) {
            return;
        }

        log.debug("flushing MondrianConnection: " + element.getKey());
        MondrianConnectionSchemaParameters monConnParams = (MondrianConnectionSchemaParameters) element.getValue();
        getOlapManagementService().flushConnection(monConnParams);
    }

    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
        // don't care
    }

    public void notifyRemoveAll(Ehcache cache) {
        // don't care
    }

    public Object clone() throws CloneNotSupportedException {
        return this.clone();
    }

    public OlapManagementService getOlapManagementService() {
        return olapManagementService;
    }

    public void setOlapManagementService(OlapManagementService olapManagementService) {
        this.olapManagementService = olapManagementService;
    }
}
