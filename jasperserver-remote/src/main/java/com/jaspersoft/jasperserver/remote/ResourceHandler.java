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

package com.jaspersoft.jasperserver.remote;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.api.search.SearchFilter;

import java.util.Map;

/**
 * The porpuse of a resource handler is to provide a pluggable way to describe
 * unknown resources.
 * The best (and suggested) way to implement an new resource is to extend the class
 * AbstractResourceHandler.
 *
 * @author Giulio Toffoli (giulio@jaspersoft.com)
 */
public interface ResourceHandler {

    /**
     * The resource type is used by the handlers registry to create new resources.
     * The class to return is the Resource class implementation.
     *
     * @return
     */
    public Class getResourceType();

    /**
     * Any custom filtering can be added to the search request via SearchFilter. All search parameters are placed to the method input.
     *
     * @param uri - Must be a valid uri, it must exists and the user must have access to it.
     * @param queryString - text string to search
     * @param wsTypes - Can be null. It is the list of wsType allowed in the returned resources.
     * @param recursive - enables/disables recursively search
     * @param maxItems - max number of items
     * @param startIndex - start index of requested page
     * @return custom resource specific search filter
     */
    SearchFilter getSearchFilter(String uri, String queryString, String wsTypes, boolean recursive, int maxItems, int startIndex);

    /**
     * Given a Repository resource this method provides a ResourceDescriptor.
     * There is some default information describing a resource (i.e. name, lable, description, uri, type),
     * and a set of properties which better describes the resource.
     * <p/>
     * At any time this method can access the current service context (AbstractService.getContext()) to
     * optionally store attachments, get attachments and use the RepositoryService.
     * <p/>
     * The passed options Map can be used by the specific resource descriptor to decide how to
     * describe itself. I.e. a input control based on a query may return in its descriptor the values
     * that should be displayed when it is proposed to the user.
     *
     * @param resource
     * @param options
     * @return
     * @throws ServiceException
     */
    public ResourceDescriptor get(Resource resource, Map options) throws ServiceException;

    /**
     * The update method knows how to update a resource. It does not save it, it just modify it.
     * This method is used to prepare a Resource before actually saving it.
     * <p/>
     * At any time this method can access the current service context (AbstractService.getContext()) to
     * optionally store attachments, get attachments and use the RepositoryService.
     * <p/>
     * <p/>
     * It may be null. If null, the resource may be new, or not yet loaded from the repository
     *
     * @param descriptor
     * @param options
     * @param save       In some cases, we just want to update the resource without actually saving it, since the
     *                   resource may be saved later. It's the case of a report unit that creates its local resources
     *                   and then saves them.
     * @return
     * @throws ServiceException
     */
    public ResourceDescriptor update(ResourceDescriptor descriptor, Map options, boolean save) throws ServiceException;

    /**
     * delete the resource described by the descriptor from repository
     *
     * @param descriptor
     * @return
     * @throws ServiceException
     */
    public void delete(ResourceDescriptor descriptor) throws ServiceException;
}
