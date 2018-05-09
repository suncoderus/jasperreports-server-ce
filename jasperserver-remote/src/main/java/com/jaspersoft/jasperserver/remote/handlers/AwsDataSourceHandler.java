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

package com.jaspersoft.jasperserver.remote.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author vsabadosh
 */
@Service
public class AwsDataSourceHandler extends JdbcDataSourceHandler {

    public Class getResourceType() {
        return AwsReportDataSource.class;
    }

    protected void doGet(Resource resource, ResourceDescriptor descriptor, Map options) throws ServiceException
    {
        super.doGet(resource, descriptor, options);

        AwsReportDataSource dsResource = (AwsReportDataSource) resource;
        descriptor.setAwsAccessKey(dsResource.getAWSAccessKey());
        descriptor.setAwsSecretKey(dsResource.getAWSSecretKey());
        descriptor.setAwsRoleARN(dsResource.getRoleARN());
        descriptor.setAwsRegion(dsResource.getAWSRegion());
        descriptor.setAwsDbName(dsResource.getDbName());
        descriptor.setAwsDbService(dsResource.getDbService());
        descriptor.setAwsDbInstanceIdentifier(dsResource.getDbInstanceIdentifier());
        descriptor.setWsType(ResourceDescriptor.TYPE_DATASOURCE_AWS);
    }

    @Override
    protected void updateResource(Resource resource, ResourceDescriptor descriptor, Map options)
    {
        super.updateResource(resource, descriptor, options);

        AwsReportDataSource awsReportDataSource = (AwsReportDataSource) resource;
        awsReportDataSource.setAWSAccessKey(descriptor.getAwsAccessKey());
        awsReportDataSource.setAWSSecretKey(descriptor.getAwsSecretKey());
        awsReportDataSource.setRoleARN(descriptor.getAwsRoleARN());
        awsReportDataSource.setAWSRegion(descriptor.getAwsRegion());
        awsReportDataSource.setDbName(descriptor.getAwsDbName());
        awsReportDataSource.setDbService(descriptor.getAwsDbService());
        awsReportDataSource.setDbInstanceIdentifier(descriptor.getAwsDbInstanceIdentifier());
    }

}
