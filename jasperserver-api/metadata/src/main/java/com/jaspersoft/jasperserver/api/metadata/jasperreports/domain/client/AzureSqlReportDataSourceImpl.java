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

package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AzureSqlReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;

public class AzureSqlReportDataSourceImpl extends JdbcReportDataSourceImpl implements AzureSqlReportDataSource {

    private static final long serialVersionUID = -3561229455787471028L;

    private String subscriptionId;
    private ResourceReference keyStoreResource;
    private String keyStoreType;
    private String keyStorePassword;
    private String serverName;
    private String dbName;

    public AzureSqlReportDataSourceImpl() {
        super();
    }

    protected Class getImplementingItf() {
        return AzureSqlReportDataSource.class;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public ResourceReference getKeyStoreResource() {
        return keyStoreResource;
    }

    public void setKeyStoreResource(ResourceReference keyStoreResource) {
        this.keyStoreResource = keyStoreResource;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {this.dbName = dbName; }

}
