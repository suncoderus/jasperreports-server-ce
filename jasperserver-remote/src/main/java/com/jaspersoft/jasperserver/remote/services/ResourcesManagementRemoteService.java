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
package com.jaspersoft.jasperserver.remote.services;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.ResourceHandler;
import com.jaspersoft.jasperserver.remote.ServiceException;

import java.util.Map;

/**
 * Facade service for resources management.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public interface ResourcesManagementRemoteService {
    /**
     * Get handler for given resource.
     *
     * @param resource - target resource
     * @return handler for given resource
     */
    ResourceHandler getHandler(Resource resource);

    /**
     * Get handler for given wsType.
     * @param wsType - target wsType
     * @return handler for given wsType
     */
    ResourceHandler getHandler(String wsType);

    /**
     * Copy resource
     *
     * @param sourceURI - source URI
     * @param destinationURI - destination URI
     */
    void copyResource(String sourceURI, String destinationURI);

     /**
     * Move resource
     *
     * @param sourceURI - source URI
     * @param destinationURI - destination URI
     */
    void moveResource(String sourceURI, String destinationURI);

    /**
     * Return a descriptor
     * Attachments, if requested, are placed inside the attachments list.
     * If the list is null, no attachment is returned.
     * the local should be true ONLY if the first call from RESTResource is for a local resource
     *
     * @param resourceURI - the resource URI
     * @param options - options of reading
     * @return the resource
     * @throws ServiceException
     */
    ResourceDescriptor getResource(String resourceURI, Map<String, Object> options) throws ServiceException;

    /**
     * Find a resource from an uri. This method is extensively used by the services to
     * manage the repository.
     *
     * @param resourceURI - the resource URI
     * @param runtimeExecutionContext - execution context
     * @return
     */
    Resource locateResource(String resourceURI, ExecutionContext runtimeExecutionContext) throws ServiceException;

    /**
     * Find a resource from an uri. This method is extensively used by the services to
     * manage the repository.
     *
     * @param uri - the resource URI
     * @return the resource
     * @throws ServiceException
     */
    Resource locateResource(String uri) throws ServiceException;

    /**
     * Delete resource
     *
     * @param uri - URI of the resource to delete
     * @throws ServiceException
     */
    void deleteResource(String uri) throws ServiceException;

    /**
     * Create new resource.
     *
     *
     * @param res_descriptor - the resource to create
     * @return created resource
     * @throws ServiceException
     */
    ResourceDescriptor putResource(ResourceDescriptor res_descriptor) throws ServiceException;

    /**
     * Build a ResourceDescriptor from a Resource.
     * The real type of this resource is saved in WsType
     *
     * @param resource - source resource
     * @param options - resource options
     * @return resource descriptor
     * @throws ServiceException
     */
    ResourceDescriptor createResourceDescriptor(Resource resource, Map options) throws ServiceException;

    /**
     * the same as createResourceDescriptor( resource, null)
     * @param resource - source resource
     * @return resource descriptor
     * @throws ServiceException
     */
    ResourceDescriptor createResourceDescriptor(Resource resource) throws ServiceException;

    /**
     * Build a ResourceDescriptor from a ResourceReference with given options.
     *
     * @param reference - reference to source resource
     * @param options - resource options
     * @return resource descriptor
     * @throws ServiceException
     */
    ResourceDescriptor createResourceDescriptor(ResourceReference reference, Map options) throws ServiceException;

    /**
     * Build a ResourceDescriptor for the resource of given URI
     *
     * @param uri - source resource URI
     * @return resource descriptor
     * @throws ServiceException
     */
    ResourceDescriptor createResourceDescriptor(String uri) throws ServiceException;

    /**
     * Build a ResourceDescriptor for the resource of given URI with options
     *
     * @param uri - source resource URI
     * @param options - resource options
     * @return resource descriptor
     * @throws ServiceException
     */
    ResourceDescriptor createResourceDescriptor(String uri, Map options) throws ServiceException;

    /**
     * updates the resource name and attributes but not location.
     *
     * @param resourceURI - URI of the resource
     * @param newRd - updated resource
     * @param save - In some cases, we just want to update the resource without actually saving it, since the
     *             resource may be saved later. It's the case of a report unit that creates its local resources
     *             and then saves them.
     */
    void updateResource(String resourceURI, ResourceDescriptor newRd, boolean save);
}
