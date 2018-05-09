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
package com.jaspersoft.jasperserver.api.logging.diagnostic.datasource;

import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.impl.DiagnosticDataProvider;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import org.springframework.context.ApplicationContext;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author vsabadosh
 */
public class DiagnosticCustomDataSourceService implements ReportDataSourceService {

    JRDataSource ds;
    private Map propertyMap;

    public Map getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(Map propertyMap) {
        this.propertyMap = propertyMap;
    }

    public DiagnosticCustomDataSourceService() {
    }

    public DiagnosticCustomDataSourceService(JRDataSource ds) {
        this.ds = ds;
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
        ApplicationContext ctx = StaticApplicationContext.getApplicationContext();
        List<List<Object>> diagnosticAttributesList = new LinkedList<List<Object>>();
        if (ctx.containsBean("diagnosticDataProvider")) {
            DiagnosticDataProvider dataProvider = ctx.getBean("diagnosticDataProvider", DiagnosticDataProvider.class);
            diagnosticAttributesList = dataProvider.getDiagnosticAttributesList();
        }

        parameterValues.put(JRParameter.REPORT_DATA_SOURCE, new DiagnosticCustomDataSource(diagnosticAttributesList));
    }

}
