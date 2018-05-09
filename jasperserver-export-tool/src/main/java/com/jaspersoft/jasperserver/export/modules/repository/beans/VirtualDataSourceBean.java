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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class VirtualDataSourceBean extends ResourceBean {

	private String timezone;
    private Map<String, ResourceReferenceBean> dataSourceUriMap = new HashMap<String, ResourceReferenceBean>();


	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		VirtualReportDataSource ds = (VirtualReportDataSource) res;
        copyDataSourcesFrom(ds, referenceHandler);
		setTimezone(ds.getTimezone());
	}

    protected void copyDataSourcesFrom(VirtualReportDataSource ds,
			ResourceExportHandler exportHandler) {
		Map dsSources = ds.getDataSourceUriMap();
		if (dsSources == null || dsSources.isEmpty()) {
			dataSourceUriMap = null;
		} else {
			dataSourceUriMap = new LinkedHashMap();
			for (Iterator it = dsSources.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				String alias = (String) entry.getKey();
				ResourceReference ref = (ResourceReference) entry.getValue();
				ResourceReferenceBean refBean = exportHandler.handleReference(ref);
				dataSourceUriMap.put(alias, refBean);
			}
		}
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		VirtualReportDataSource ds = (VirtualReportDataSource) res;
		copyDataSourcesTo(ds, importHandler);
		ds.setTimezone(getTimezone());
	}

    protected void copyDataSourcesTo(VirtualReportDataSource ds, ResourceImportHandler importHandler) {
		Map<String, ResourceReference> dsSources = new LinkedHashMap<String, ResourceReference>();
		if (dataSourceUriMap != null && !dataSourceUriMap.isEmpty()) {
			for (Iterator it = dataSourceUriMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				String alias = (String) entry.getKey();
				ResourceReferenceBean refBean = (ResourceReferenceBean) entry.getValue();
				ResourceReference ref = importHandler.handleReference(refBean);
				dsSources.put(alias, ref);
			}
		}
		ds.setDataSourceUriMap(dsSources);
	}

    public Map<String, ResourceReferenceBean> getDataSourceUriMap() {
        return dataSourceUriMap;
    }

    public void setDataSourceUriMap(Map<String, ResourceReferenceBean> dataSourceUriMap) {
        this.dataSourceUriMap = dataSourceUriMap;
    }

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

}
