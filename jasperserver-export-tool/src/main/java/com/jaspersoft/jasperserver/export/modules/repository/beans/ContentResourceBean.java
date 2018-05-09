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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;


/**
 * @author tkavanagh
 * @version $Id$
 */
public class ContentResourceBean extends ResourceBean {

	public static final String DATA_PROVIDER_ID = "contentResourceData";

	private String fileType;
	private ContentResourceBean[] resources;
	private String dataFile;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		ContentResource contentRes = (ContentResource) res;
		setFileType(contentRes.getFileType());
		setDataFile(exportHandler.handleData(res, DATA_PROVIDER_ID));
		
		copyResourcesFrom(contentRes, exportHandler);
	}

	protected void copyResourcesFrom(ContentResource contentRes, ResourceExportHandler exportHandler) {
		List subResources = contentRes.getResources();
		if (subResources == null || subResources.isEmpty()) {
			resources = null;
		} else {
			resources = new ContentResourceBean[subResources.size()];
			int c = 0;
			for (Iterator it = subResources.iterator(); it.hasNext(); ++c) {
				ContentResource subResource = (ContentResource) it.next();
				resources[c] = (ContentResourceBean) exportHandler.handleResource(subResource);
			}
		}
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		ContentResource contentRes = (ContentResource) res;
		contentRes.setFileType(getFileType());
		if (dataFile != null) {
			contentRes.setData(importHandler.handleData(this, dataFile, DATA_PROVIDER_ID));
		}
		
		copyResourcesTo(contentRes, importHandler);
	}

	protected void copyResourcesTo(ContentResource contentRes, ResourceImportHandler importHandler) {
		List subResources;
		if (resources == null) {
			subResources = null;
		} else {
			subResources = new ArrayList(resources.length);
			for (int i = 0; i < resources.length; i++) {
				ContentResourceBean resource = resources[i];
				ContentResource subResource = (ContentResource) importHandler.handleResource(resource);
				subResources.add(subResource);
			}
		}

		contentRes.setResources(subResources);
	}

	public String getFileType() {
		return fileType;
	}
	
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public ContentResourceBean[] getResources() {
		return resources;
	}

	public void setResources(ContentResourceBean[] resources) {
		this.resources = resources;
	}

	public String getDataFile() {
		return dataFile;
	}

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}
	
}
