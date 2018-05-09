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

package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoCustomDataSource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class CustomDataSourceBean extends ResourceBean {

	private String serviceClass;
	private Map propertyMap;
    private Map<String, ResourceReferenceBean> resources = new HashMap<String, ResourceReferenceBean>();

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		CustomReportDataSource ds = (CustomReportDataSource) res;
        copyResourcesFrom(ds, referenceHandler);
		setServiceClass(ds.getServiceClass());

		Map dsProperties = ds.getPropertyMap();
		if (dsProperties == null) {
			setPropertyMap(new HashMap());
		} else {
			Map<String, String> copyDsPropertyMap = new HashMap<String, String>(dsProperties);

			//encrypt for export
			//TODO: in the future, encryption should be done with an asymmetric public key from the TARGET server
			//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved to encryption engine
			String pwd = copyDsPropertyMap.get(RepoCustomDataSource.PASSWORD_DS_PARAM);
			if (pwd != null && pwd.trim().length() > 0)
				copyDsPropertyMap.put(RepoCustomDataSource.PASSWORD_DS_PARAM,
						ENCRYPTION_PREFIX + importExportCipher.encode(pwd) + ENCRYPTION_SUFFIX);
			setPropertyMap(copyDsPropertyMap);
		}
	}

    protected void copyResourcesFrom(CustomReportDataSource ds,
			ResourceExportHandler exportHandler) {
		Map dsResources = ds.getResources();
		if (dsResources == null || dsResources.isEmpty()) {
			resources = null;
		} else {
			resources = new LinkedHashMap();
			for (Iterator it = dsResources.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				String alias = (String) entry.getKey();
				ResourceReference ref = (ResourceReference) entry.getValue();
				ResourceReferenceBean refBean = exportHandler.handleReference(ref);
				resources.put(alias, refBean);
			}
		}
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		CustomReportDataSource ds = (CustomReportDataSource) res;
        copyResourcesTo(ds, importHandler);
		ds.setServiceClass(getServiceClass());

		final Map dsPropertyMap = getPropertyMap();
		if (dsPropertyMap == null) {
			ds.setPropertyMap(new HashMap());
		} else {
			Map<String, String> copyDsPropertyMap = new HashMap<String, String>(dsPropertyMap);

			//decrypt pwd for import. if decryption fails, set password as is; this is probably due to legacy import
			//TODO: in the future, decryption should be done with an asymmetric private key from THIS server
			//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved inside encrypt()/decrypt() in encryption engine
			String pwd = copyDsPropertyMap.get(RepoCustomDataSource.PASSWORD_DS_PARAM);
			if (pwd != null && pwd.trim().length() > 0)
				copyDsPropertyMap.put(RepoCustomDataSource.PASSWORD_DS_PARAM,
						(pwd.startsWith(ENCRYPTION_PREFIX) && pwd.endsWith(ENCRYPTION_SUFFIX)) ?
							importExportCipher.decode(pwd.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(ENCRYPTION_SUFFIX + "$", "")) : pwd);

			ds.setPropertyMap(copyDsPropertyMap);
		}
	}

    protected void copyResourcesTo(CustomReportDataSource ds, ResourceImportHandler importHandler) {
		Map<String, ResourceReference> dsResources = new LinkedHashMap<String, ResourceReference>();
		if (resources != null && !resources.isEmpty()) {
			for (Iterator it = resources.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				String alias = (String) entry.getKey();
				ResourceReferenceBean refBean = (ResourceReferenceBean) entry.getValue();
				ResourceReference ref = importHandler.handleReference(refBean);
				dsResources.put(alias, ref);
			}
		}
		ds.setResources(dsResources);
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public Map getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map propertyMap) {
		this.propertyMap = propertyMap;
	}

    public Map<String, ResourceReferenceBean> getResources() {
        return resources;
    }

    public void setResources(Map<String, ResourceReferenceBean> resources) {
        this.resources = resources;
    }
}
