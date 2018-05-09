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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.azuresql;

import java.io.IOException;
import java.io.InputStream;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JdbcDataSourceService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JdbcReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AwsProperties;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AzureSqlReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * Factory producing a Report Data Source Service for an Azure SQL Report Data Source.
 * The Report Data Source Services produced by this factory are capable of autocorrecting Azure SQL server firewall rules.
 */
class AzureSqlReportDataSourceServiceFactory extends JdbcReportDataSourceServiceFactory {

    private AwsProperties azureProperties;
    private String defaultKeyStoreType;
    private String defaultJdbcDriverClassName;
    private RepositoryService repositoryService;
    private MessageSource messageSource;
    private AzureSqlManagementService azureSqlManagementService;

    @Override
    public ReportDataSourceService createService(ReportDataSource reportDs) {
        if (!(reportDs instanceof AzureSqlReportDataSource)) {
            throw new JSException("jsexception.invalid.azureSql.datasource", new Object[] { reportDs.getClass() });
        }

        AzureSqlReportDataSource azureSqlReportDs = (AzureSqlReportDataSource) reportDs;

        DataSource sqlDs = getPoolDataSource(getDriverClass(azureSqlReportDs),
                getConnectionUrl(azureSqlReportDs),
                azureSqlReportDs.getUsername(),
                azureSqlReportDs.getPassword());

        if (azureProperties.isSecurityGroupChangesEnabled()) {
            sqlDs = getAutoRecoveringDs(azureSqlReportDs, sqlDs);
        }

        return new JdbcDataSourceService(sqlDs, getTimeZoneByDataSourceTimeZone(azureSqlReportDs.getTimezone()));
    }

    private AutoRecoveringAzureSqlDataSourceDecorator getAutoRecoveringDs(AzureSqlReportDataSource azureSqlReportDs,
                                                                          DataSource sqlDs) {
        AutoRecoveringAzureSqlDataSourceDecorator autoRecoveringDs = new AutoRecoveringAzureSqlDataSourceDecorator(sqlDs);
        autoRecoveringDs.setMessageSource(messageSource);
        autoRecoveringDs.setAzureSqlManagementService(azureSqlManagementService);
        autoRecoveringDs.setAzureManagementCredentials(createAzureManagementCredentials(azureSqlReportDs));
        autoRecoveringDs.setServerName(azureSqlReportDs.getServerName());
        autoRecoveringDs.setRuleName(azureProperties.getSecurityGroupName());
        autoRecoveringDs.setPublicIpAddress(azureProperties.getSecurityGroupIngressPublicIp());
        return autoRecoveringDs;
    }

    private AzureManagementCredentials createAzureManagementCredentials(AzureSqlReportDataSource azureSqlReportDs) {
        String keyStoreType = getKeyStoreType(azureSqlReportDs);
        byte[] keyStoreBytes = getKeyStoreBytes(azureSqlReportDs);

        AzureManagementCredentials creds = new AzureManagementCredentials();
        creds.setSubscriptionId(azureSqlReportDs.getSubscriptionId());
        creds.setKeyStoreType(keyStoreType);
        creds.setKeyStoreBytes(keyStoreBytes);
        creds.setKeyStorePassword(azureSqlReportDs.getKeyStorePassword().toCharArray());
        return creds;
    }

    private String getConnectionUrl(AzureSqlReportDataSource azureSqlReportDs) {
        if (azureSqlReportDs.getConnectionUrl() == null || azureSqlReportDs.getConnectionUrl().isEmpty()) {
            String newJDBCUrl = azureSqlManagementService.getJdbcUrl(azureSqlReportDs.getServerName(), azureSqlReportDs.getDbName());
            azureSqlReportDs.setConnectionUrl(newJDBCUrl);
        }
        return azureSqlReportDs.getConnectionUrl();
    }

    private String getDriverClass(AzureSqlReportDataSource azureSqlReportDs) {
        if (azureSqlReportDs.getDriverClass() == null || azureSqlReportDs.getDriverClass().isEmpty()) {
            azureSqlReportDs.setDriverClass(defaultJdbcDriverClassName);
        }
        return azureSqlReportDs.getDriverClass();
    }

    private byte[] getKeyStoreBytes(AzureSqlReportDataSource azureSqlReportDs) {
        byte[] keyStoreBytes;
        InputStream data = null;
        try {
            ResourceReference resourceReference = azureSqlReportDs.getKeyStoreResource();
            FileResource fileResource = null;
            if (resourceReference.isLocal()) {
                fileResource = (FileResource) resourceReference.getLocalResource();
            } else {
                fileResource = (FileResource) repositoryService.getResource(null, resourceReference.getReferenceURI(),
                        com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource.class);
            }
            if (fileResource.hasData()) {
                data = fileResource.getDataStream();
            } else {
                FileResourceData resourceData = repositoryService.getResourceData(null, fileResource.getURIString());
                data = resourceData.getDataStream();
            }
            keyStoreBytes = IOUtils.toByteArray(data);
        } catch (IOException e) {
            throw new JSException(messageSource.getMessage("azure.exception.datasource.key.error", null, LocaleContextHolder.getLocale()), e);
        } finally {
            try {
                if (data != null)
                    data.close();
            } catch (Exception ex) {
            }
            ;
        }
        return keyStoreBytes;
    }

    private String getKeyStoreType(AzureSqlReportDataSource azureSqlReportDs) {
        if (azureSqlReportDs.getKeyStoreType() == null || azureSqlReportDs.getKeyStoreType().isEmpty())
            return defaultKeyStoreType;
        else
            return azureSqlReportDs.getKeyStoreType();
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setAzureProperties(AwsProperties azureProperties) {
        this.azureProperties = azureProperties;
    }

    public void setDefaultKeyStoreType(String defaultKeyStoreType) {
        this.defaultKeyStoreType = defaultKeyStoreType;
    }

    public void setDefaultJdbcDriverClassName(String defaultJdbcDriverClassName) {
        this.defaultJdbcDriverClassName = defaultJdbcDriverClassName;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setAzureSqlManagementService(AzureSqlManagementService azureSqlManagementService) {
        this.azureSqlManagementService = azureSqlManagementService;
    }
}
