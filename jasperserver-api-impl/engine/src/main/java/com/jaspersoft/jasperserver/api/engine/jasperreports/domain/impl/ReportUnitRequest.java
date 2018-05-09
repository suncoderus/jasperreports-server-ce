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
package com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl;

import java.util.Map;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ReportExecuter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: AbstractAttributedObject.java 2140 2006-02-21 06:41:21Z tony $
 */
public class ReportUnitRequest extends ReportUnitRequestBase
{
	
	private String reportUnitUri = null;
    private Map<String, Object> propertyMap = null;

	/**
	 * 
	 */
	public ReportUnitRequest(String reportUnitUri, Map reportParameters)
	{
		this(reportUnitUri, reportParameters, null);
	}

    public ReportUnitRequest(String reportUnitUri, Map reportParameters, Map propertyMap)
	{
		super(reportParameters);
		this.reportUnitUri = reportUnitUri;
        this.propertyMap = propertyMap;
	}

	/**
	 * 
	 */
	public String getReportUnitUri()
	{
		return reportUnitUri;
	}

    public Map<String, Object> getPropertyMap() {
        return propertyMap;
    }

    public ReportUnitResult execute(ExecutionContext context, ReportExecuter executer) {
		return executer.executeReportUnitRequest(context, this);
	}
}
