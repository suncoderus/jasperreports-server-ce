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

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id$
 */

/**
 * A FileResource typically holds data (unless it in a "link" (isReference = true)).
 * For these bean objects, they do not hold their own data. Instead,
 * the data is written to disk. The stringURI field inherited from ResourceBean
 * is uses to locate the data once it is time to import the bean objects 
 * back into JasperServer and recreate copies of the original java objects.
 * 
 */
public class FileResourceBean extends ResourceBean {

	public static final String DATA_PROVIDER_ID = "fileResourceData";
	
	/*
	 * The following come from the FileResource interface 
	 */
	private String fileType;
	private String dataFile;
	private String referenceUri;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		FileResource fileRes = (FileResource) res;
		setFileType(fileRes.getFileType());
		
		setDataFile(exportHandler.handleData(res, DATA_PROVIDER_ID));
		
		String reference = fileRes.getReferenceURI();
		setReferenceUri(reference);
		if (reference != null) {
			exportHandler.queueResource(reference);
		}
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		FileResource fileRes = (FileResource) res;
		fileRes.setFileType(getFileType());

		if (dataFile != null) {
			fileRes.setData(importHandler.handleData(this, dataFile, DATA_PROVIDER_ID));
		}

		if (referenceUri != null) {
			fileRes.setReferenceURI(importHandler.handleResource(referenceUri));
		}
	}

	public String getFileType() {
		return fileType;
	}
	
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public String getReferenceUri() {
		return referenceUri;
	}
	
	public void setReferenceUri(String referenceUri) {
		this.referenceUri = referenceUri;
	}

	public String getDataFile() {
		return dataFile;
	}

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}
}
