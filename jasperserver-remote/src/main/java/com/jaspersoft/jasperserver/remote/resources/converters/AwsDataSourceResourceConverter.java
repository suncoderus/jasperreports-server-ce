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

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientAwsDataSource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class AwsDataSourceResourceConverter extends GenericJdbcDataSourceResourceConverter<AwsReportDataSource, ClientAwsDataSource> {
    @Override
    protected AwsReportDataSource resourceSpecificFieldsToServer(ClientAwsDataSource clientObject, AwsReportDataSource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        final AwsReportDataSource awsReportDataSource = super.resourceSpecificFieldsToServer(clientObject, resultToUpdate, options);
        awsReportDataSource.setRoleARN(clientObject.getRoleArn());
        awsReportDataSource.setAWSAccessKey(clientObject.getAccessKey());
        awsReportDataSource.setAWSRegion(clientObject.getRegion());
        if (clientObject.getSecretKey() != null || clientObject.getAccessKey() == null) {
            awsReportDataSource.setAWSSecretKey(clientObject.getSecretKey());
        }
        awsReportDataSource.setDbInstanceIdentifier(clientObject.getDbInstanceIdentifier());
        awsReportDataSource.setDbName(clientObject.getDbName());
        awsReportDataSource.setDbService(clientObject.getDbService());
        return awsReportDataSource;
    }

    @Override
    protected ClientAwsDataSource resourceSpecificFieldsToClient(ClientAwsDataSource client, AwsReportDataSource serverObject, ToClientConversionOptions options) {
        final ClientAwsDataSource clientAwsDataSource = super.resourceSpecificFieldsToClient(client, serverObject, options);
        clientAwsDataSource.setRoleArn(serverObject.getRoleARN());
        clientAwsDataSource.setAccessKey(serverObject.getAWSAccessKey());
        clientAwsDataSource.setRegion(serverObject.getAWSRegion());
        clientAwsDataSource.setDbInstanceIdentifier(serverObject.getDbInstanceIdentifier());
        clientAwsDataSource.setDbName(serverObject.getDbName());
        clientAwsDataSource.setDbService(serverObject.getDbService());
        return clientAwsDataSource;
    }
}
