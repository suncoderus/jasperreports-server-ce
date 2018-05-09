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
package com.jaspersoft.jasperserver.api.metadata.olap.service.impl;

import java.util.Map;

import net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuterFactory;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class XmlaConnectionDataSourceService implements ReportDataSourceService {
    
	private final XMLAConnection xmlaConnection;
	private final String tenantSeparator;
	private final User contextUser;
	
	public XmlaConnectionDataSourceService(XMLAConnection xmlaConnection, String tenantSeparator) {
		this(xmlaConnection, tenantSeparator, null);
	}
	
	public XmlaConnectionDataSourceService(XMLAConnection xmlaConnection, String tenantSeparator,
			User contextUser) {
		this.xmlaConnection = xmlaConnection;
		this.tenantSeparator = tenantSeparator;
		this.contextUser = contextUser;
	}

	public void setReportParameterValues(Map parameterValues) {
		parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_URL, xmlaConnection.getURI());
		parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_CATALOG, xmlaConnection.getCatalog());
        String dataSource = xmlaConnection.getDataSource();
        
        String uName = null;
        String uPass = null;
		if (contextUser == null) {
			uName = xmlaConnection.getUsername();
			uPass = xmlaConnection.getPassword();
		} else {
			uName = contextUser.getUsername();
			if (contextUser.getTenantId() != null) {
				uName += tenantSeparator + contextUser.getTenantId();
			}
			uPass = contextUser.getPassword();
		}
        
		parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_USER, uName);
		parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_PASSWORD, uPass);
		parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_DATASOURCE, dataSource);
	}

	public void closeConnection() {
	}

}
