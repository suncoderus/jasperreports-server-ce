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

package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.JdbcReportDataSourceImpl;

@JasperServerAPI
public interface AzureSqlReportDataSource extends JdbcReportDataSource {

    public String getSubscriptionId();

    public void setSubscriptionId(String subscriptionId);

    public ResourceReference getKeyStoreResource();

    public void setKeyStoreResource(ResourceReference keyStoreResource);

    public String getKeyStorePassword();

    public void setKeyStorePassword(String keyStorePassword);

    public String getKeyStoreType();

    public void setKeyStoreType(String keyStoreType);

    public String getServerName();

    public void setServerName(String serverName);

    public String getDbName();

    public void setDbName(String dbName);
}

