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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.ParameterContributor;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.ParameterContributorFactory;

import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepoRepositoryService.java 45608 2014-05-12 09:49:27Z lchirita $
 */
public class DataSourceWrapperParameterContributorFactory implements ParameterContributorFactory 
{
	/**
	 *
	 */
	public static final String PROPERTY_DATA_SOURCE_LOCATION = "com.jaspersoft.jrs.data.source";

	private RepositoryService repositoryService;
	private EngineService engineService;
			

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public EngineService getEngineService() {
		return engineService;
	}

	public void setEngineService(EngineService engineService) {
		this.engineService = engineService;
	}

	/**
	 *
	 */
	public List<ParameterContributor> getContributors(ParameterContributorContext context) throws JRException
	{
		List<ParameterContributor> contributors = new ArrayList<ParameterContributor>();

		String dataSourceUri = JRPropertiesUtil.getInstance(context.getJasperReportsContext()).getProperty(context.getDataset(), PROPERTY_DATA_SOURCE_LOCATION); 
		if (dataSourceUri != null)
		{
			ReportDataSource dataSource = (ReportDataSource)repositoryService.getResource(null, dataSourceUri, Resource.class);
			if (dataSource == null)
			{
				throw new JRException("Data source " + dataSourceUri + "not found!");
			}
				
			ReportDataSourceService dataSourceService = engineService.createDataSourceService(dataSource);
			ParameterContributor parameterContributor = new DataSourceWrapperParameterContributor(dataSourceService);
			
			return Collections.singletonList(parameterContributor);
		}

		return contributors;
	}
}
