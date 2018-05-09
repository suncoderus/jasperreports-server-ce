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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AzureSqlReportDataSource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author vsabadosh
 */
public class AzureSqlDataSourceBean extends JdbcDataSourceBean {

    private String subscriptionId;
    private ResourceReferenceBean keyStoreResource;
    private String keyStorePassword;
    private String keyStoreType;
    private String serverName;
    private String dbName;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		super.additionalCopyFrom(res, referenceHandler);

        AzureSqlReportDataSource ds = (AzureSqlReportDataSource) res;

        setKeyStoreResource(referenceHandler.handleReference(ds.getKeyStoreResource()));
        //encrypt for export
		//TODO: in the future, encryption should be done with an asymmetric public key from the TARGET server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved to encryption engine
        setSubscriptionId(ENCRYPTION_PREFIX + importExportCipher.encode(ds.getSubscriptionId()) + ENCRYPTION_SUFFIX);
		setKeyStorePassword(ENCRYPTION_PREFIX + importExportCipher.encode(ds.getKeyStorePassword()) + ENCRYPTION_SUFFIX);
        setKeyStoreType(ds.getKeyStoreType());
        setServerName(ds.getServerName());
		setDbName(ds.getDbName());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		super.additionalCopyTo(res, importHandler);

        AzureSqlReportDataSource ds = (AzureSqlReportDataSource) res;

        ds.setKeyStoreResource(importHandler.handleReference(getKeyStoreResource()));
        ds.setServerName(getServerName());
		ds.setDbName(getDbName());
        ds.setKeyStoreType(getKeyStoreType());

		final String subscriptionIdKey = getSubscriptionId();
		final String keyStorePasswordKey = getKeyStorePassword();

		//decrypt keys for import. if decryption fails, set keys as is; this is probably due to legacy import
		//TODO: in the future, decryption should be done with an asymmetric private key from THIS server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved inside encrypt()/decrypt() in encryption engine
		ds.setSubscriptionId((subscriptionIdKey != null && subscriptionIdKey.startsWith(ENCRYPTION_PREFIX) && subscriptionIdKey.endsWith(ENCRYPTION_SUFFIX)) ?
                importExportCipher.decode(subscriptionIdKey.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(ENCRYPTION_SUFFIX + "$", "")) : subscriptionIdKey);
		ds.setKeyStorePassword((keyStorePasswordKey != null && keyStorePasswordKey.startsWith(ENCRYPTION_PREFIX) && keyStorePasswordKey.endsWith(ENCRYPTION_SUFFIX)) ?
			importExportCipher.decode(keyStorePasswordKey.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(ENCRYPTION_SUFFIX + "$", "")) : keyStorePasswordKey);
	}

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public ResourceReferenceBean getKeyStoreResource() {
        return keyStoreResource;
    }

    public void setKeyStoreResource(ResourceReferenceBean keyStoreResource) {
        this.keyStoreResource = keyStoreResource;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
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

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }
}
