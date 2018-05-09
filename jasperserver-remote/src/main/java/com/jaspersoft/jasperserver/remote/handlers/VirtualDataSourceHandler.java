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
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("remoteVirtualDataSourceHandler")
public class VirtualDataSourceHandler extends RepositoryResourceHandler {

    public Class getResourceType() {
        return VirtualReportDataSource.class;
    }

    protected void doGet(Resource resource, ResourceDescriptor descriptor, Map options) throws ServiceException
    {
        VirtualReportDataSource dsResource = (VirtualReportDataSource) resource;
        descriptor.setWsType(ResourceDescriptor.TYPE_DATASOURCE_VIRTUAL);
        for(Map.Entry<String, ResourceReference> entry: dsResource.getDataSourceUriMap().entrySet()) {
            ResourceDescriptor rdDs = new ResourceDescriptor();
            rdDs.setWsType(ResourceDescriptor.TYPE_DATASOURCE);
            rdDs.setReferenceUri(entry.getValue().getReferenceURI());
            rdDs.setIsReference(true);
            rdDs.setResourceProperty(ResourceDescriptor.PROP_DATASOURCE_SUB_DS_ID, entry.getKey());
            descriptor.getChildren().add(rdDs);
        }
    }

    @Override
    protected void updateResource(Resource resource, ResourceDescriptor descriptor, Map options)
    {
        super.updateResource(resource, descriptor, options);
        VirtualReportDataSource dsResource = (VirtualReportDataSource) resource;
        dsResource.getDataSourceUriMap().clear();
        for(ResourceDescriptor subResource: (List<ResourceDescriptor>)descriptor.getChildren()) {
            String subDsId = subResource.getResourcePropertyValue(ResourceDescriptor.PROP_DATASOURCE_SUB_DS_ID);
            if(!ResourceDescriptor.TYPE_DATASOURCE.equals(subResource.getWsType()) || subDsId == null) {
                continue; //not a subDS
            }
            ResourceReference rr = new ResourceReference(subResource.getReferenceUri());
            dsResource.getDataSourceUriMap().put(subDsId, rr);
        }
    }

  
}
