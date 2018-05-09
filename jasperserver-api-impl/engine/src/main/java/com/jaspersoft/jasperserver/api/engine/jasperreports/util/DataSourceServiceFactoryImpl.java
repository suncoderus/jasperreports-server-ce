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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.service.impl.BeanForInterfaceImplementationFactoryImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DataSourceServiceFactoryImpl extends
		BeanForInterfaceImplementationFactoryImpl implements
		DataSourceServiceFactory, InitializingBean {

	private static final Log log = LogFactory.getLog(DataSourceServiceFactoryImpl.class);
	
	// the list of DataSourceServiceDefinitions is now injected by type,
	// so we don't need to explicitly set them or use a bean updater.
	@Autowired
	private List<DataSourceServiceDefinition> serviceDefinitionList;
	
	private Map<String, DataSourceServiceDefinition> serviceDefinitionMap;
	
	private Set<Class<?>> universalTypes;
	private Map languageTypes;
	
	public void afterPropertiesSet() {
		// init serviceDefinitionMap from serviceDefinitionList
		serviceDefinitionMap = new LinkedHashMap<String, DataSourceServiceDefinition>();
		for (DataSourceServiceDefinition dssd : serviceDefinitionList) {
			serviceDefinitionMap.put(dssd.getDataSourceInterface(), dssd);
		}
		setBeanInterfaceMappings();
		collectLanguageTypes();
	}

	protected void setBeanInterfaceMappings() {
		Map beanItfMap = new HashMap();
		for (Iterator it = serviceDefinitionMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String itf = (String) entry.getKey();
			DataSourceServiceDefinition serviceDef = (DataSourceServiceDefinition) entry.getValue();
			beanItfMap.put(itf, serviceDef.getServiceBeanName());
		}
		setBeanForInterfaceMappings(beanItfMap);
	}

	protected void collectLanguageTypes() {
		languageTypes = new HashMap();
		universalTypes = new HashSet();
		
		for (Iterator it = serviceDefinitionMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String itf = (String) entry.getKey();
			DataSourceServiceDefinition serviceDef = (DataSourceServiceDefinition) entry.getValue();
			
			if (serviceDef.isAnyLanguage()) {
				universalTypes.add(resolveType(itf));
			}
			
			collectTypes(itf, serviceDef);
		}
	}

	protected void collectTypes(String itf, DataSourceServiceDefinition serviceDef) {
		Set languages = serviceDef.getSupportedQueryLanguages();
		if (languages != null) {
			for (Iterator langIt = languages.iterator(); langIt.hasNext();) {
				String language = (String) langIt.next();
				Set langTypes = (Set) languageTypes.get(language);
				if (langTypes == null) {
					langTypes = new HashSet();
					languageTypes.put(language, langTypes);
				}
				langTypes.add(resolveType(itf));
			}
		}
	}

	protected Class<?> resolveType(String itf) {
		try {
			return Class.forName(itf, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

	public Set getSupportingDataSourceTypes(String queryLanguage) {
		Set types = new HashSet();
		Set langTypes = (Set) languageTypes.get(queryLanguage);
		if (langTypes != null) {
			types.addAll(langTypes);
		}
		types.addAll(universalTypes);
		return types;
	}

	public Map<String, DataSourceServiceDefinition> getServiceDefinitionMap() {
		return serviceDefinitionMap;
	}

	public List<DataSourceServiceDefinition> getServiceDefinitionList() {
		return serviceDefinitionList;
	}

	public void setServiceDefinitionList(List<DataSourceServiceDefinition> serviceDefinitionList) {
		this.serviceDefinitionList = serviceDefinitionList;
	}

}
