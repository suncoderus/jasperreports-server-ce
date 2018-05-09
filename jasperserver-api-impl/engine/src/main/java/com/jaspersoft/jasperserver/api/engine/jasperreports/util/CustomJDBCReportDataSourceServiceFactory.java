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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.VirtualDataSourceHandler;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JdbcDataSourceService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JdbcReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.VirtualReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceAcessDeniedException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.*;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.JdbcReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.VirtualReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.io.*;
import java.sql.DriverManager;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ichan
 * Date: 1/15/15
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomJDBCReportDataSourceServiceFactory extends VirtualReportDataSourceServiceFactory implements CustomDelegatedDataSourceServiceFactory, CustomJdbcReportDataSourceProvider {

	private static final Log log = LogFactory.getLog(CustomJDBCReportDataSourceServiceFactory.class);
    VirtualDataSourceHandler virtualDataSourceHandler;
    public static  String IS_WRAPPED_DATASOURCE = "IS_WRAPPED_DATASOURCE";

    public void setCustomDataSourceDefinition(CustomDataSourceDefinition dsDef) {
        // do nothing
    }
    public VirtualDataSourceHandler getVirtualDataSourceHandler() {
        return virtualDataSourceHandler;
    }

    public void setVirtualDataSourceHandler(VirtualDataSourceHandler virtualDataSourceHandler) {
        this.virtualDataSourceHandler = virtualDataSourceHandler;
    }

    public ReportDataSourceService createService(ReportDataSource reportDataSource) {
        if (!(reportDataSource instanceof CustomReportDataSource)) {
            throw new JSException("jsexception.invalid.jdbc.datasource", new Object[] {reportDataSource.getClass()});
        }
        // master data source - virtual data source
        DataSource ds;
        VirtualReportDataSource virtualDataSource = (VirtualReportDataSource) getWrappedReportDataSource((CustomReportDataSource)reportDataSource);
        TimeZone timeZone = getTimeZoneByDataSourceTimeZone(virtualDataSource.getTimezone());
        try {
            // generate JDBC data source from virtual data source
            ds = virtualDataSourceHandler.getSqlDataSource(ExecutionContextImpl.getRuntimeExecutionContext(), virtualDataSource);
            return new JdbcDataSourceService(ds, timeZone);
        } catch (JSResourceAcessDeniedException accessDeniedEx) {
            if (log.isDebugEnabled()) log.debug(accessDeniedEx, accessDeniedEx);
            throw accessDeniedEx;
        } catch (Exception e) {
            if (log.isDebugEnabled())
                log.debug(e, e);
            throw new JSExceptionWrapper(e);
        }

    }

    public VirtualReportDataSource getWrappedReportDataSource(CustomReportDataSource customReportDataSource) {
        VirtualReportDataSourceImpl virtualReportDataSourceImpl = new VirtualReportDataSourceImpl();
        Map<String, ResourceReference> resourceMap = new HashMap<String, ResourceReference>();
        resourceMap.put(customReportDataSource.getName(), new ResourceReference(customReportDataSource.getURIString()));
        virtualReportDataSourceImpl.setDataSourceUriMap(resourceMap);
        String timeZone = ((String) customReportDataSource.getPropertyMap().get("timeZone"));
        virtualReportDataSourceImpl.setTimezone(timeZone);
        return virtualReportDataSourceImpl;
    }


}
