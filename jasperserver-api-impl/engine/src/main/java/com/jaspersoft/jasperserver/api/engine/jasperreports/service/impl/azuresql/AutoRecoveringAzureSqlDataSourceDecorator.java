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

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.AutoRecoveringDataSourceDecorator;

/**
 * This decorator sets up Azure SQL Server firewall rules as a corrective action in case of connection failure.
 */
class AutoRecoveringAzureSqlDataSourceDecorator extends AutoRecoveringDataSourceDecorator {

    private AzureManagementCredentials azureManagementCredentials;
    private String serverName;
    private String ruleName;
    private String ipAddress;
    private static int NO_FIREWALL_RULE_ERROR = 40615;
    private MessageSource messageSource;
    private AzureSqlManagementService azureSqlManagementService;

    public AutoRecoveringAzureSqlDataSourceDecorator(DataSource target) {
        super(target);
    }

    @Override
    protected boolean recover(SQLException cause) {
        if (cause.getErrorCode() != NO_FIREWALL_RULE_ERROR) {
            return false; // cannot recover from this error
        }
        // check all arguments
        checkArgNotEmpty(serverName, "azure.exception.datasource.recovery.server.name.not.provided");
        checkArgNotEmpty(ruleName, "azure.exception.datasource.recovery.firewall.rule.name.not.provided");
        checkArgNotEmpty(ipAddress, "azure.exception.datasource.recovery.public.ip.not.provided");

        try {
            azureSqlManagementService.ensureFirewallRule(azureManagementCredentials, serverName, ruleName, ipAddress);
        } catch (Exception e) {
            String localizedMessage = messageSource.getMessage("azure.exception.datasource.cannot.recover.datasource",null, LocaleContextHolder.getLocale());
            throw new JSException(localizedMessage, e);
        }
        return true; // recovery attempt made
    }

    public void setAzureManagementCredentials(AzureManagementCredentials azureManagementCredentials) {
        if (azureManagementCredentials == null) {
            throw new IllegalArgumentException("azureManagementCredentials must not be empty");
        }
        this.azureManagementCredentials = azureManagementCredentials;
    }

    public void setServerName(String serverName) {
        // it's not normal for a data source to not have server name, so fail early here
        checkArgNotEmpty(serverName, "azure.exception.datasource.recovery.server.name.not.provided");
        this.serverName = serverName;
    }

    public void setRuleName(String ruleName) {
        // rule name is a JRS global property
        // it's ok for it to become empty (JRS global settings are modified) after a data source is created
        // so no argument check here
        this.ruleName = ruleName;
    }

    public void setPublicIpAddress(String ipAddress) {
        // public ip address is a JRS global property
        // it's ok for it to become empty (JRS global settings are modified) after a data source is created
        // so no argument check here
        this.ipAddress = ipAddress;
    }

    private void checkArgNotEmpty(String value, String errorCode) {
        if (value == null || value.isEmpty()) {
            throw new JSException(messageSource.getMessage(errorCode, null, LocaleContextHolder.getLocale()));
        }
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setAzureSqlManagementService(AzureSqlManagementService azureSqlManagementService) {
        this.azureSqlManagementService = azureSqlManagementService;
    }
}
