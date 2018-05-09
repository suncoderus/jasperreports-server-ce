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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource;

import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoDataSource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author swood
 * 
 * @hibernate.joined-subclass table="BeanDatasource"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoCustomDataSource extends RepoDataSource implements
		RepoReportDataSource {
	private static final String CDS_NAME_PROPERTY = "_cds_name";
	public static final String PASSWORD_DS_PARAM = "password";

	private String serviceClass;

	private Map propertyMap;

	private Map<String, RepoResource> resources;

	public RepoCustomDataSource() {
		super();
	}

	protected Class getClientItf() {
		return CustomReportDataSource.class;
	}

	public Map getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map propertyMap) {
		this.propertyMap = propertyMap;
	}

	public Map<String, RepoResource> getResources() {
		return resources;
	}

	public void setResources(Map<String, RepoResource> resources) {
		this.resources = resources;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);

		CustomReportDataSource ds = (CustomReportDataSource) clientRes;

		Map aPropertyMap = new HashMap(getPropertyMap());

		String password = (String) aPropertyMap.get(PASSWORD_DS_PARAM);
		if (password != null && password.trim().length() > 0) {
			aPropertyMap.put(PASSWORD_DS_PARAM, PasswordCipherer.getInstance().decodePassword(password));
		}
		// set ds name from property
		ds.setDataSourceName((String) aPropertyMap.get(CDS_NAME_PROPERTY));
		ds.setPropertyMap(aPropertyMap);
		ds.setServiceClass(getServiceClass());
		final Map<String, RepoResource> resourcesMap = getResources();
		if(resourcesMap != null && !resourcesMap.isEmpty()){
			final HashMap<String, ResourceReference> clientResources = new HashMap<String, ResourceReference>(resourcesMap.size());
			for(String key : resourcesMap.keySet()){
				clientResources.put(key, getClientReference(resourcesMap.get(key), resourceFactory));
			}
			ds.setResources(clientResources);
		}
	}

	protected void copyFrom(Resource clientRes,
			ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		CustomReportDataSource ds = (CustomReportDataSource) clientRes;

		Map properties = ds.getPropertyMap() == null ? new HashMap() : ds.getPropertyMap();
		
		String password = (String) properties.get(PASSWORD_DS_PARAM);
		if (password != null && password.trim().length() > 0) {
			properties.put(PASSWORD_DS_PARAM,	PasswordCipherer.getInstance().encodePassword(password));
		}
		// store ds name as property
		String dsName = ds.getDataSourceName();
		if (dsName != null && dsName.trim().length() > 0) {
            properties.put(CDS_NAME_PROPERTY, dsName);
		}

		setPropertyMap(properties);
		setServiceClass(ds.getServiceClass());
		final Map<String, ResourceReference> clientResources = ds.getResources();
		if(clientResources != null && !clientResources.isEmpty()){
			Map<String, RepoResource> serverResources = new HashMap<String, RepoResource>(clientResources.size());
			for(String key : clientResources.keySet()){
				serverResources.put(key, getReference(clientResources.get(key), RepoResource.class, referenceResolver));
			}
			setResources(serverResources);
		}
	}
}
