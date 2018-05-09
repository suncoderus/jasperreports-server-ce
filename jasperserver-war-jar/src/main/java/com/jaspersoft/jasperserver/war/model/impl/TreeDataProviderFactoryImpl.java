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
package com.jaspersoft.jasperserver.war.model.impl;

import java.util.Map;

import com.jaspersoft.jasperserver.war.model.TreeDataProvider;
import com.jaspersoft.jasperserver.war.model.TreeDataProviderFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Implements TreeDataProviderFactory.
 * It is Spring configuration driven factory.
 * @author asokolnikov
 *
 */
public class TreeDataProviderFactoryImpl implements TreeDataProviderFactory, ApplicationContextAware {
    private ApplicationContext applicationContext;

    // providers map
    private Map<String, String> treeProvidersMap;

    /**
     * Returns TreeDataProviderFactory instance by type
     * @param type requested protived type
     */
    public TreeDataProvider getDataProvider(String type) {
        return applicationContext.getBean(treeProvidersMap.get(type), TreeDataProvider.class);
    }

    public Map getTreeProvidersMap() {
        return treeProvidersMap;
    }

    public void setTreeProvidersMap(Map<String, String> treeProvidersMap) {
        this.treeProvidersMap = treeProvidersMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
