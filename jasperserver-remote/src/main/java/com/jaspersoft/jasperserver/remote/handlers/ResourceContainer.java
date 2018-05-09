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

/**
 * A resource which contains nested resources (like a Report Unit) may implement the
 * ResourceContainer interface to easily manager (put and remove) sub resources.
 *
 * @author gtoffoli
 */
public interface ResourceContainer {


        /**
         * Notified the container that a new resource is not attached to it.
         * It is not necessary to save the resource.
         *
         * @param parent
         * @param resourceToSet
         * @return
         * @throws ServiceException
         */
        public Resource addSubResource(Resource parent, ResourceDescriptor resourceToSet) throws ServiceException;

        /**
         * Delete a resource from a parent resource
         * Wehn you implement this method, it is not necessary to save the parent once the resource has been removed.
         * The caller of this method is in charge to save changes.
         * It is not necessary to delete the removed resource also.
         *
         * @param parent
         * @param childDescriptor
         * @throws ServiceException
         */
	public  void deleteSubResource(Resource parent, ResourceDescriptor childDescriptor) throws ServiceException;

}
