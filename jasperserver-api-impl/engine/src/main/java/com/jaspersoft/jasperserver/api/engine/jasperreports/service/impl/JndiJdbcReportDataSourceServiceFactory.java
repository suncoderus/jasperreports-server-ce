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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.sql.DataSource;

import com.jaspersoft.jasperserver.api.common.util.TibcoDriverManager;
import com.jaspersoft.jasperserver.api.metadata.common.util.JndiFallbackResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * @author swood
 *
 */
public class JndiJdbcReportDataSourceServiceFactory extends JdbcReportDataSourceServiceFactory {

	private static final Log log = LogFactory.getLog(JndiJdbcReportDataSourceServiceFactory.class);
	private Context ctx = null;
	private boolean disableJndi;
	private JndiFallbackResolver jndiFallbackResolver;
    private static Map<String, String> jndiAppServerConnectionFunctionMap;

	public JndiJdbcReportDataSourceServiceFactory() {
		try {
			
			// Set the context here, as it is a heavyweight constructor

			ctx = new InitialContext();
		} catch (NamingException e) {
			log.error(e);
			throw new JSException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory#createService(com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
	 */
	public ReportDataSourceService createService(ReportDataSource dataSource) {
        String jndiName;
        DataSource ds;
        JndiJdbcReportDataSource jndiDataSource = (JndiJdbcReportDataSource) dataSource;
        TimeZone timeZone = getTimeZoneByDataSourceTimeZone(jndiDataSource.getTimezone());
        jndiName = jndiDataSource.getJndiName();
        if (disableJndi || ctx == null) {
        	return createServiceFromFallbackProperties(jndiName, timeZone);
        }
        try {
			ds = (DataSource) ctx.lookup("java:comp/env/" + jndiName);
			return new JdbcDataSourceService(ds, timeZone);
		} catch (NoInitialContextException e) {
			// there's no context provider available...fall back to prop lookup
			ctx = null;
			return createService(dataSource);
		} catch (NamingException e) {
            //Added as short time solution due of http://bugzilla.jaspersoft.com/show_bug.cgi?id=26570.
            //The main problem - this code executes in separate tread (non http).
            //Jboss 7 support team recommend that you use the non-component environment namespace for such situations.
            try {
                ds = (DataSource) ctx.lookup(jndiName);
                return new JdbcDataSourceService(ds, timeZone);
            } catch (NamingException ex) {
                if (log.isDebugEnabled())
                    log.debug(e, e);
                throw new JSExceptionWrapper(e);
            }
		}
	}

	/**
	 * The buildomatic file js.jdbc.properties has all the info for standard datasources. 
	 * Here's an example:
	 * 
	 * # jasperserver repo db settings
	 * metadata.jdbc.driverClassName=com.mysql.jdbc.Driver
	 * metadata.jdbc.url=jdbc:mysql://localhost:3306/js_trunk?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&autoReconnectForPools=true
	 * metadata.jdbc.username=root 
	 * metadata.jdbc.password=mysql
	 * metadata.jndi=jdbc/jasperserver
	 * 
	 * We use this info to map the jndi to jdbc, then create the datasource with the superclass
	 * 
	 * @param jndiName
	 * @param timeZone
	 * @return
	 */
	private ReportDataSourceService createServiceFromFallbackProperties(String jndiName, TimeZone timeZone) {
        Map<String, String> jdbcMap = jndiFallbackResolver.getJdbcPropertiesMap(jndiName);

		DataSource datasource = getPoolDataSource(
                jdbcMap.get(JndiFallbackResolver.JDBC_DRIVER_CLASS_NAME),
                jdbcMap.get(JndiFallbackResolver.JDBC_URL),
                jdbcMap.get(JndiFallbackResolver.JDBC_USERNAME),
                jdbcMap.get(JndiFallbackResolver.JDBC_PASSWORD));

        return new JdbcDataSourceService(datasource, timeZone);
		
	}
	
	public void setDisableJndi(boolean disableJndi) {
		this.disableJndi = disableJndi;
	}

	public boolean isDisableJndi() {
		return disableJndi;
	}

    public void setJndiFallbackResolver(JndiFallbackResolver jndiFallbackResolver) {
        this.jndiFallbackResolver = jndiFallbackResolver;
    }

    public static Map<String, String> getJndiAppServerConnectionFunctionMap() {
        return jndiAppServerConnectionFunctionMap;
}

    public static void setJndiAppServerConnectionFunctionMap(Map<String, String> jndiAppServerConnectionFunctionMap) {
        JndiJdbcReportDataSourceServiceFactory.jndiAppServerConnectionFunctionMap = jndiAppServerConnectionFunctionMap;
    }

}
