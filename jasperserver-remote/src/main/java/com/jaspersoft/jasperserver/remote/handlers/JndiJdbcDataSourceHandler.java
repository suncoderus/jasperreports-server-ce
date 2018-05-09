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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author gtoffoli
 * @version $Id$
 */
@Service
public class JndiJdbcDataSourceHandler extends RepositoryResourceHandler {

    public Class getResourceType() {
        return JndiJdbcReportDataSource.class;
    }

    protected void doGet(Resource resource, ResourceDescriptor descriptor, Map options) throws ServiceException {
        JndiJdbcReportDataSource dsResource = (JndiJdbcReportDataSource) resource;
        descriptor.setJndiName(dsResource.getJndiName());
        descriptor.setWsType(ResourceDescriptor.TYPE_DATASOURCE_JNDI);
    }

    @Override
    protected void updateResource(Resource resource, ResourceDescriptor descriptor, Map options) {
        super.updateResource(resource, descriptor, options);

        JndiJdbcReportDataSource jndiJdbcReportDataSource = (JndiJdbcReportDataSource) resource;
        jndiJdbcReportDataSource.setJndiName(descriptor.getJndiName());
    }

   
}
