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
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author gtoffoli
 * @version $Id$
 */
@Service
public class XmlaConnectionHandler extends RepositoryResourceHandler {

    public Class getResourceType() {
        return XMLAConnection.class;
    }

    protected void doGet(Resource resource, ResourceDescriptor descriptor, Map options) throws ServiceException {
        XMLAConnection connection = (XMLAConnection) resource;
        descriptor.setWsType(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION);
        descriptor.setHasData(false);
        descriptor.setIsReference(false);
        descriptor.setResourceProperty(ResourceDescriptor.PROP_XMLA_URI,
                connection.getURI());
        descriptor.setResourceProperty(ResourceDescriptor.PROP_XMLA_CATALOG,
                connection.getCatalog());
        descriptor.setResourceProperty(ResourceDescriptor.PROP_XMLA_DATASOURCE,
                connection.getDataSource());
        descriptor.setResourceProperty(ResourceDescriptor.PROP_XMLA_USERNAME,
                connection.getUsername());
        descriptor.setResourceProperty(ResourceDescriptor.PROP_XMLA_PASSWORD,
                connection.getPassword());
    }

    @Override
    protected void updateResource(Resource resource, ResourceDescriptor descriptor, Map options) {

        super.updateResource(resource, descriptor, options);

        XMLAConnection xmlaConnection = (XMLAConnection) resource;
        xmlaConnection.setURI(descriptor.getResourcePropertyValue(ResourceDescriptor.PROP_XMLA_URI));
        xmlaConnection.setCatalog(descriptor.getResourcePropertyValue(ResourceDescriptor.PROP_XMLA_CATALOG));
        xmlaConnection.setDataSource(descriptor.getResourcePropertyValue(ResourceDescriptor.PROP_XMLA_DATASOURCE));
        xmlaConnection.setUsername(descriptor.getResourcePropertyValue(ResourceDescriptor.PROP_XMLA_USERNAME));
        xmlaConnection.setPassword(descriptor.getResourcePropertyValue(ResourceDescriptor.PROP_XMLA_PASSWORD));
    }


}
