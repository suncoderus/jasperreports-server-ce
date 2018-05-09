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
package com.jaspersoft.jasperserver.remote.resources.converters;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AzureSqlReportDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientAzureSqlDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableFile;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;

@Service
public class AzureSqlDataSourceResourceConverter extends GenericJdbcDataSourceResourceConverter<AzureSqlReportDataSource, ClientAzureSqlDataSource> {
    @Resource
    protected ResourceReferenceConverterProvider resourceReferenceConverterProvider;

    @Override
    protected AzureSqlReportDataSource resourceSpecificFieldsToServer(ClientAzureSqlDataSource clientObject, AzureSqlReportDataSource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        final AzureSqlReportDataSource azureSqlReportDataSource = super.resourceSpecificFieldsToServer(clientObject, resultToUpdate, options);
        azureSqlReportDataSource.setSubscriptionId(clientObject.getSubscriptionId());
        azureSqlReportDataSource.setKeyStoreResource(convertResourceToServer(clientObject.getKeyStoreUri(), options));
        // modify password only if use explicitly modifies it (clientObject.getKeyStorePassword()!=null) or if subscription id is erased.
        if (clientObject.getKeyStorePassword() != null || clientObject.getSubscriptionId() == null) {
            azureSqlReportDataSource.setKeyStorePassword(clientObject.getKeyStorePassword());
        }
        azureSqlReportDataSource.setServerName(clientObject.getServerName());
        azureSqlReportDataSource.setDbName(clientObject.getDbName());
        return azureSqlReportDataSource;
    }

    @Override
    protected ClientAzureSqlDataSource resourceSpecificFieldsToClient(ClientAzureSqlDataSource client, AzureSqlReportDataSource serverObject, ToClientConversionOptions options) {
        final ClientAzureSqlDataSource clientAwsDataSource = super.resourceSpecificFieldsToClient(client, serverObject, options);
        clientAwsDataSource.setSubscriptionId(serverObject.getSubscriptionId());
        clientAwsDataSource.setKeyStoreUri(convertResourceToClient(serverObject.getKeyStoreResource(), options));
        clientAwsDataSource.setKeyStorePassword(serverObject.getKeyStorePassword());
        clientAwsDataSource.setServerName(serverObject.getServerName());
        clientAwsDataSource.setDbName(serverObject.getDbName());
        return clientAwsDataSource;
    }

    private ResourceReference convertResourceToServer(String uri, ToServerConversionOptions options) {
        ResourceReferenceConverter<ClientReferenceableFile> referenceConverter = resourceReferenceConverterProvider
                .getConverterForType(ClientReferenceableFile.class);
        return referenceConverter.toServer(new ClientReference(uri), options);
    }
    
    private String convertResourceToClient(ResourceReference resource, ToClientConversionOptions options) {
        if (resource == null) {
            return null;
        }
        ResourceReferenceConverter<ClientReferenceableFile> referenceConverter = resourceReferenceConverterProvider
                .getConverterForType(ClientReferenceableFile.class);
        return referenceConverter.toClient(resource, options).getUri();
    }

}
