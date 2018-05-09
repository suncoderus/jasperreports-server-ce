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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author gtoffoli
 * @version $Id$
 */
@Service
public class QueryHandler extends RepositoryResourceHandler {

    public Class getResourceType() {
        return Query.class;
    }

    protected void doGet(Resource resource, ResourceDescriptor descriptor, Map options) throws ServiceException {

        Query fileResource = (Query) resource;
        descriptor.setWsType(ResourceDescriptor.TYPE_QUERY);
        descriptor.setHasData(false);
        descriptor.setIsReference(false);
        descriptor.setSql(fileResource.getSql());
        descriptor.setResourceProperty(ResourceDescriptor.PROP_QUERY_LANGUAGE,
                fileResource.getLanguage());

        // Get datasource...
        ResourceReference rref = fileResource.getDataSource();
        if (rref != null) {
            ResourceDescriptor childRd = null;

            if (rref.isLocal()) {
                childRd = getResourcesManagementRemoteService().createResourceDescriptor(rref.getLocalResource());
            } else {
                childRd = new ResourceDescriptor();
                childRd.setWsType(ResourceDescriptor.TYPE_DATASOURCE);
                childRd.setReferenceUri(rref.getReferenceURI());
                childRd.setIsReference(true);
            }

            if (childRd != null) {
                descriptor.getChildren().add(childRd);
            }
        }
    }

    @Override
    protected void updateResource(Resource resource, ResourceDescriptor descriptor, Map options) {
        super.updateResource(resource, descriptor, options);

        Query query = (Query) resource;

        query.setSql(descriptor.getSql());

        // Update the datasource....
        for (int i = 0; i < descriptor.getChildren().size(); ++i) {
            ResourceDescriptor childResource = (ResourceDescriptor) descriptor.getChildren().get(i);
            if (isDataSource(childResource)) {
                if (childResource.getIsReference()) {
                    query.setDataSourceReference(childResource.getReferenceUri());
                } else {
                    ReportDataSource datasource = (ReportDataSource) createChildResource(childResource);
                    query.setDataSource(datasource);
                }
            }
        }

        String lang = descriptor.getResourcePropertyValue(ResourceDescriptor.PROP_QUERY_LANGUAGE);
        if (lang == null || lang.length() == 0) {
            lang = "sql";
        }
        query.setLanguage(lang);
    }

   
}
