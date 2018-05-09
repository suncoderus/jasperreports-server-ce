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

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import java.util.Properties;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 * Manage Mondrian Connection cache updates of the cache from the cluster.
 *
 * @author swood
 */
public class MondrianConnectionSharedCacheEventListener implements CacheEventListener {

    private static final Log log = LogFactory.getLog(MondrianConnectionSharedCacheEventListener.class);

    public MondrianConnectionSharedCacheEventListener(ApplicationContext ctx, Properties properties) {

        Properties springBeanNameConfiguration = null;
        try {
            springBeanNameConfiguration = ((Properties) ctx.getBean("springConfiguration"));
        } catch (NoSuchBeanDefinitionException e) {
            springBeanNameConfiguration = new Properties();
        }

        String repositoryServiceName = "repositoryService";
        if (springBeanNameConfiguration.containsKey("bean.repositoryService")) {
            repositoryServiceName = springBeanNameConfiguration.getProperty("bean.repositoryService");
        }

        RepositoryService repositoryService = (RepositoryService) ctx.getBean(repositoryServiceName);

    }

    public void dispose() {
    }

    public void notifyElementEvicted(Ehcache cache, Element element) {
    }

    public void notifyElementExpired(Ehcache cache, Element element) {
    }

    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
    }

    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
    }

    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
    }

    public void notifyRemoveAll(Ehcache cache) {
    }

    public Object clone() throws CloneNotSupportedException {
        return this.clone();
    }

}
