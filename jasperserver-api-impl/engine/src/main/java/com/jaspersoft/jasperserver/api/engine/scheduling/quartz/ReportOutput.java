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

package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import java.util.ArrayList;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.GzipDataContainer;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id$
 *
 */
public class ReportOutput {
	private final DataContainer data;
	private final String fileType;
	private final String filename;
	private final List children;
	private String repositoryPath;
	
	public ReportOutput(DataContainer data, String fileType, String filename) {
		this.data = data;
		this.fileType = fileType;
		this.filename = filename;
		this.children = new ArrayList();
	}

	public DataContainer getData() {
		return data;
	}

	public String getFilename() {
		return filename;
	}

	public String getFileType() {
		return fileType;
	}
	
	public List getChildren() {
		return children;
	}
	
	public void addChild(ReportOutput child) {
		children.add(child);
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public DataContainer getPersistenceData() {
		return DataContainerStreamUtil.getRawDataContainer(data);
	}
	
}