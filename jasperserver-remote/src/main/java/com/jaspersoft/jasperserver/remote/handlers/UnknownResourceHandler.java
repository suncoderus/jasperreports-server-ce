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
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author gtoffoli
 * @version $Id$
 */
@Service
public class UnknownResourceHandler extends RepositoryResourceHandler {

    private final static Log log = LogFactory.getLog(UnknownResourceHandler.class);

    public Class getResourceType() {
        return Resource.class;
    }

    protected void doGet(Resource resource, ResourceDescriptor descriptor, Map options) throws ServiceException {
        descriptor.setWsType(ResourceDescriptor.TYPE_UNKNOW);
        if (log.isWarnEnabled()) {
            log.warn("Unknow resource type: "
                + resource.getResourceType() + " instanceof "
                + resource.getClass().getName());
        }
    }

    @Override
    protected void updateResource(Resource resource, ResourceDescriptor descriptor, Map options) {
        super.updateResource(resource, descriptor, options);


    }

}
