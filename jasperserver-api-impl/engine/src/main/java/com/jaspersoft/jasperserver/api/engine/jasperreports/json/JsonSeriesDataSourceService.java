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
package com.jaspersoft.jasperserver.api.engine.jasperreports.json;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.query.JsonQueryExecuterFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryUtil;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JsonSeriesDataSourceService implements ReportDataSourceService {
	
	private static final Log log = LogFactory.getLog(JsonSeriesDataSourceService.class);

	private String folder;
	private String jsonResourcePattern;
	private RepositoryService repository;

	public JsonSeriesDataSourceService() {
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public void setJsonResourcePattern(String jsonResourcePattern) {
		this.jsonResourcePattern = jsonResourcePattern;
	}

	@Override
	public void setReportParameterValues(Map parameterValues) {
		RepositoryContext repositoryContext = RepositoryUtil.getThreadRepositoryContext();
		RepositoryService repositoryService = repositoryContext == null ? repository : repositoryContext.getRepository();
		
		List<String> locations = JsonSeriesUtil.findJsonResources(repositoryService, folder, jsonResourcePattern);
		parameterValues.put(JsonQueryExecuterFactory.JSON_SOURCES, locations);
	}

	@Override
	public void closeConnection() {
	}

}
