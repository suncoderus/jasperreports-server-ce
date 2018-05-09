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
import java.net.InetAddress;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.core.util.StringUtil;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.management.sql.FirewallRuleOperations;
import com.microsoft.windowsazure.management.sql.SqlManagementClient;
import com.microsoft.windowsazure.management.sql.models.Database;
import com.microsoft.windowsazure.management.sql.models.FirewallRuleCreateParameters;
import com.microsoft.windowsazure.management.sql.models.FirewallRuleUpdateParameters;
import com.microsoft.windowsazure.management.sql.models.Server;

/**
 * Utility class for high level Azure SQL Management operations.
 */
public class AzureSqlManagementService {

    private String defaultJdbcUrlSyntax;

    /**
     * Returns JDBC URL string constructed from server name and database name.
     * @param serverName - the Azure SQL server name
     * @param dbName - the Azure SQL database name
     * @return JDBC URL
     */
    public String getJdbcUrl(String serverName, String dbName) {
        String newJDBCUrl = StringUtil.replace(defaultJdbcUrlSyntax, "%SERVERNAME", serverName);
        return StringUtil.replace(newJDBCUrl, "%DBNAME", dbName);
    }

    /**
     * Ensures the presence of a firewall rule allowing connections from the provided ip address to the Azure SQL server with the given name.
     * If a firewall rule with the given name does not exist then it will be created.
     * If the rule already exists then it will be updated.
     * 
     * @param azureManagementCredentials - the Azure SQL Management Credentials
     * @param serverName - the name of the Azure SQL server
     * @param ruleName - the name of the firewall rule
     * @param ipAddress - the ip address
     */
    public void ensureFirewallRule(AzureManagementCredentials azureManagementCredentials,
                                   String serverName,
                                   String ruleName,
                                   String ipAddress) {
        checkManagementCredentials(azureManagementCredentials);
        checkArgNotEmpty(serverName, "azure.exception.datasource.recovery.server.name.not.provided");
        checkArgNotEmpty(ruleName, "azure.exception.datasource.recovery.firewall.rule.name.not.provided");
        checkArgNotEmpty(ipAddress, "azure.exception.datasource.recovery.public.ip.not.provided");

        try {
            SqlManagementClient azureClient = new SqlManagementClientBuilder()
                    .subscriptionId(azureManagementCredentials.getSubscriptionId())
                    .keyStoreType(azureManagementCredentials.getKeyStoreType())
                    .keyStoreBytes(azureManagementCredentials.getKeyStoreBytes())
                    .keyStorePassword(azureManagementCredentials.getKeyStorePassword()).build();
            try {
                createOrUpdateFirewallRule(azureClient, serverName, ruleName, ipAddress);
            } finally {
                azureClient.close();
            }
        } catch (Exception e) {
            throw getException("azure.exception.datasource.cannot.ensure.firewall.rule", e);
        }
    }

    private void createOrUpdateFirewallRule(SqlManagementClient azureClient, String serverName, String ruleName, String ipAddress) throws Exception {
        FirewallRuleOperations firewallRuleOperations = azureClient.getFirewallRulesOperations();
        InetAddress ip = InetAddress.getByName(ipAddress);
        try {
            // try to create a rule first as it is likely the most frequent scenario
            firewallRuleOperations.create(serverName, new FirewallRuleCreateParameters(ruleName, ip, ip));
        } catch (ServiceException e) {
            // looks like a rule with this name already exists, try to update it then
            firewallRuleOperations.update(serverName, ruleName, new FirewallRuleUpdateParameters(ruleName, ip, ip));
        }
    }

    /**
     * Returns a map where the key is Azure SQL server name and the value is a set of database names.
     * Never returns <code>null</code> or <code>null</code> values in the collection.
     * 
     * @param azureManagementCredentials - the builder used to create the client instance
     * @return a map of server names to set of database names
     */
    public ImmutableMap<String, ImmutableSet<String>> getDatabases(AzureManagementCredentials azureManagementCredentials) {
        checkManagementCredentials(azureManagementCredentials);
        try {
            SqlManagementClient azureClient = new SqlManagementClientBuilder()
                    .subscriptionId(azureManagementCredentials.getSubscriptionId())
                    .keyStoreType(azureManagementCredentials.getKeyStoreType())
                    .keyStoreBytes(azureManagementCredentials.getKeyStoreBytes())
                    .keyStorePassword(azureManagementCredentials.getKeyStorePassword()).build();
            try {
                return getServerAndDbPairs(azureClient);
            } finally {
                azureClient.close();
            }
        } catch (Exception e) {
            if (e instanceof ServiceException && "ForbiddenError".equals(((ServiceException)e).getErrorCode())) {
                throw new JSException("azure.exception.datasource.auth.error", e);
            }
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if (rootCause instanceof GeneralSecurityException ||
                    rootCause instanceof IOException) {
                throw new JSException("azure.exception.datasource.key.error", e);
            }
            throw getException("azure.exception.datasource.cannot.retrieve.database.list", e);
        }
    }

    private ImmutableMap<String, ImmutableSet<String>> getServerAndDbPairs(SqlManagementClient azureClient) throws Exception {
        ImmutableMap.Builder<String, ImmutableSet<String>> server2Databases = ImmutableMap.builder();
        for (Server server : azureClient.getServersOperations().list()) {
            if (StringUtils.isNotBlank(server.getName())) {
                ImmutableSet.Builder<String> databases = ImmutableSet.builder();
                for (Database db : azureClient.getDatabasesOperations().list(server.getName()).getDatabases()) {
                    if (StringUtils.isNotBlank(db.getName())) {
                        databases.add(db.getName());
                    }
                }
                server2Databases.put(server.getName(), databases.build());
            }
        }
        return server2Databases.build();
    }

    private void checkArgNotEmpty(String arg, String errorCode) {
        if (arg == null || arg.isEmpty()) {
            throw getException(errorCode, null);
        }
    }

    private void checkArgNotEmpty(byte[] arg, String errorCode) {
        if (arg == null || arg.length == 0) {
            throw getException(errorCode, null);
        }
    }

    private JSException getException(String errorCode, Exception originalException) {
        return new JSException(errorCode, originalException);
    }

    private void checkArgNotNull(Object arg, String argName) {
        if (arg == null) {
            throw new IllegalArgumentException(argName + " must not be null");
        }
    }

    private void checkManagementCredentials(AzureManagementCredentials creds) {
        // this is not a user error, so no translation
        checkArgNotNull(creds, "azureManagementCredentials");
        checkArgNotEmpty(creds.getSubscriptionId(), "azure.exception.datasource.recovery.subscription.id.not.provided");
        checkArgNotEmpty(creds.getKeyStoreType(), "azure.exception.datasource.recovery.key.store.type.not.provided");
        checkArgNotEmpty(creds.getKeyStoreBytes(), "azure.exception.datasource.recovery.key.store.file.not.provided");

    }

    public void setDefaultJdbcUrlSyntax(String defaultJdbcUrlSyntax) {
        this.defaultJdbcUrlSyntax = defaultJdbcUrlSyntax;
    }
}
