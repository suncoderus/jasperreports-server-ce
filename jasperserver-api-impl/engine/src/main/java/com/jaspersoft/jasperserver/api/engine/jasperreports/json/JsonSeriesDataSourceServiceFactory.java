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

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDelegatedDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JsonSeriesDataSourceServiceFactory implements CustomDelegatedDataSourceServiceFactory {

	private RepositoryService repository;
	
	public JsonSeriesDataSourceServiceFactory() {
	}

	@Override
	public ReportDataSourceService createService(ReportDataSource dataSource) {
		CustomReportDataSource ds = (CustomReportDataSource) dataSource;
		String folder = (String) ds.getPropertyMap().get("folder");
		String jsonResourcePattern = (String) ds.getPropertyMap().get("jsonResourcePattern");
		
		JsonSeriesDataSourceService dsService = new JsonSeriesDataSourceService();
		dsService.setRepository(repository);
		dsService.setFolder(folder);
		dsService.setJsonResourcePattern(jsonResourcePattern);
		return dsService;
	}

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	@Override
	public void setCustomDataSourceDefinition(CustomDataSourceDefinition dsDef) {
	}

}
