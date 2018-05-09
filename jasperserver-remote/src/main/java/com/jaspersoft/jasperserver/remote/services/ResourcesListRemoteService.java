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

import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.ServiceException;

import java.util.List;

/**
 * Read only service for resources search.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public interface ResourcesListRemoteService {
    /**
     * Read resources list in folder of given URI or items of report unit
     *
     * @param uri - folder or report unit URI
     * @return list of resources
     * @throws ServiceException
     */
    List listResources(String uri) throws ServiceException;

    /**
     * Read resources list in folder of given URI or items of report unit
     *
     * @param uri - folder or report unit URI
     * @param maxItems - max number of items
     * @return list of resources
     * @throws ServiceException
     */
    List<ResourceDescriptor> listResources(String uri, int maxItems) throws ServiceException;

    /**
     * Get resources which satisfy the given criteria.
     * Since there is not a good or precise way to filter the type of resource,
     * this method allows to provide a specific wsType (that can be null).
     *
     * @param criteria - search criteria
     * @param maxItems - max number of items
     * @param wsTypes - Can be null. It is the list of wsType allowed in the returned resources.
     * @return list of resources matching given search criteria
     * @throws ServiceException
     */
    List<ResourceDescriptor> getResources(FilterCriteria criteria, int maxItems, List<String> wsTypes) throws ServiceException;

    /**
     *
     * @param uri - Must be a valid uri, it must exists and the user must have access to it.
     * @param queryString - text string to search
     * @param wsTypes - Can be null. It is the list of wsType allowed in the returned resources.
     * @param recursive - enables/disables recursively search
     * @param maxItems - max number of items
     * @param startIndex - start index of requested page
     * @return list of resources
     * @throws ServiceException
     */
    List getResources(String uri, String queryString, List<String> wsTypes, boolean recursive, int maxItems, int startIndex) throws ServiceException;
}
