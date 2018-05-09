/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package example.cds;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * @author swood
 *
 */
public class WebScraperDataSourceService implements ReportDataSourceService {

	private String url;
	private String path;
	
	public WebScraperDataSourceService() {
	}
	
	public WebScraperDataSourceService(JRDataSource ds) {
	}
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService#closeConnection()
	 */
	public void closeConnection() {
		// Do nothing
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService#setReportParameterValues(java.util.Map)
	 */
	public void setReportParameterValues(Map parameterValues) {
		parameterValues.put(JRParameter.REPORT_DATA_SOURCE, new WebScraperDataSource(url, path, parameterValues));
		// these are for the benefit of the cache
		parameterValues.put("webscraperURL", url);
		parameterValues.put("webscraperPath", path);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
