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
package com.jaspersoft.jasperserver.remote.connection.jdbc;

import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.BaseJdbcDataSource;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.DataSourceServiceDefinition;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.DataSourceServiceFactoryImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.remote.connection.ContextCreationFailedException;
import com.jaspersoft.jasperserver.remote.connection.datadiscovery.Connector;
import net.sf.jasperreports.engine.JRParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class JdbcConnector<ConnectionDescriptionType extends Resource> implements Connector<Connection, ConnectionDescriptionType> {
    private Map<Connection, BaseJdbcDataSource> openConnections = new HashMap<Connection, BaseJdbcDataSource>();
    @javax.annotation.Resource(name = "dataSourceServiceFactories")
    private DataSourceServiceFactoryImpl dataSourceServiceFactory;
    @Autowired
    private ApplicationContext applicationContext;
    @javax.annotation.Resource
    private SecureExceptionHandler secureExceptionHandler;
    @javax.annotation.Resource
    private MessageSource messageSource;
    @javax.annotation.Resource(name = "concreteRepository")
    private RepositoryService repository;

    @Override
    public Connection openConnection(ConnectionDescriptionType connectionDescriptor) {
        final HashMap parameterValues = new HashMap();
        BaseJdbcDataSource dataSourceService = getDataSourceService(connectionDescriptor);
        dataSourceService.setReportParameterValues(parameterValues);
        Connection connection = (Connection) parameterValues.get(JRParameter.REPORT_CONNECTION);
        openConnections.put(connection, dataSourceService);
        return connection;
    }

    @Override
    public void closeConnection(Connection connection) {
        openConnections.remove(connection).closeConnection();
    }

    public void testConnection(ConnectionDescriptionType connectionDescriptor) {
        boolean passed = false;
        Throwable exception = null;

        try {
            passed = getDataSourceService(connectionDescriptor).testConnection();
        } catch (SQLException vex) {
            if (vex.getMessage().indexOf("[JI_CONNECTION_VALID]") >= 0) passed = true;
            exception = vex;
        } catch (Throwable e) {
            exception = e;
        }
        if (!passed) {
            if (connectionDescriptor instanceof JndiJdbcReportDataSource) {
                final String jndiName = ((JndiJdbcReportDataSource) connectionDescriptor).getJndiName();
                throw new ContextCreationFailedException(jndiName, "jndiName", "Invalid JNDI name: " + jndiName,
                        exception, secureExceptionHandler);
            } else {
                throw new ContextCreationFailedException(connectionDescriptor, exception, secureExceptionHandler);
            }
        }
    }

    protected BaseJdbcDataSource getDataSourceService(ConnectionDescriptionType connectionDescription) {
        if (connectionDescription instanceof JdbcReportDataSource) {
            ensureJdbcDataSourcePassword((JdbcReportDataSource) connectionDescription);
        }
        final DataSourceServiceDefinition dataSourceServiceDefinition = dataSourceServiceFactory
                .getServiceDefinitionMap().get(connectionDescription.getResourceType());
        if (dataSourceServiceDefinition == null) {
            throw new IllegalStateException("Unsupported connection description type. Check configuration");
        }
        final ReportDataSourceServiceFactory reportDataSourceServiceFactory = applicationContext
                .getBean(dataSourceServiceDefinition.getServiceBeanName(), ReportDataSourceServiceFactory.class);
        return (BaseJdbcDataSource) reportDataSourceServiceFactory.createService((ReportDataSource) connectionDescription);
    }

    protected void ensureJdbcDataSourcePassword(JdbcReportDataSource jdbcDataSource) {
        final String password = jdbcDataSource.getPassword();
        final String passwordSubstitution = messageSource.getMessage("input.password.substitution", null, LocaleContextHolder.getLocale());
        if ((password == null || password.equals(passwordSubstitution)) && jdbcDataSource.getURIString() != null) {
            JdbcReportDataSource existingDs = (JdbcReportDataSource) repository.getResource(null, jdbcDataSource.getURIString());
            if (existingDs != null) {
                jdbcDataSource.setPassword(existingDs.getPassword());
            }
        }
    }
}
