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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource;

import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AzureSqlReportDataSource;

/**
 * Hibernate entity representing Azure SQL Data Source.
 */
public class RepoAzureSqlDataSource extends RepoJdbcDataSource {

    private String subscriptionId;
    private RepoResource keyStoreResource;
    private String keyStorePassword;
    private String keyStoreType;
    private String serverName;
    private String dbName;

    public RepoAzureSqlDataSource() {
    }

    protected Class<AzureSqlReportDataSource> getClientItf() {
        return AzureSqlReportDataSource.class;
    }

    protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
        super.copyTo(clientRes, resourceFactory);
        AzureSqlReportDataSource ds = (AzureSqlReportDataSource) clientRes;
        ds.setSubscriptionId(getSubscriptionId());
        final RepoResource resource = getKeyStoreResource();
        if(resource != null){
            ResourceReference clientResource = getClientReference(resource, resourceFactory);
            ds.setKeyStoreResource(clientResource);
        }
        ds.setKeyStorePassword(PasswordCipherer.getInstance().decodePassword(getKeyStorePassword()));
        ds.setKeyStoreType(getKeyStoreType());
        ds.setServerName(getServerName());
        ds.setDbName(getDbName());
    }

    protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
        super.copyFrom(clientRes, referenceResolver);
        AzureSqlReportDataSource ds = (AzureSqlReportDataSource) clientRes;
        setSubscriptionId(ds.getSubscriptionId());

        ResourceReference clientResource = ds.getKeyStoreResource();
        if(clientResource != null){
            RepoResource serverResource = getReference(clientResource, RepoResource.class, referenceResolver);
            setKeyStoreResource(serverResource);
        }


        setServerName(ds.getServerName());
        setKeyStorePassword(PasswordCipherer.getInstance().encodePassword(ds.getKeyStorePassword()));
        setKeyStoreType(ds.getKeyStoreType());
        setDbName(ds.getDbName());
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public RepoResource getKeyStoreResource() {
        return keyStoreResource;
    }

    public void setKeyStoreResource(RepoResource keyStoreResource) {
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

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

}
