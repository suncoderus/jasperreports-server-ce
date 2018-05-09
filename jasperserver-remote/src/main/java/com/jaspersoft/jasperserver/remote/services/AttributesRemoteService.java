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

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.remote.ServiceException;

import java.util.List;

/**
 * Implementation of this interface is a facade to internal services related to ProfileAttribute object.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public interface AttributesRemoteService {
    /**
     * Delete the attribute
     * @param userName - name of the user
     * @param pa - attribute to remove
     */
    void deleteAttribute(String userName, ProfileAttribute pa);

    /**
     * Read attribute value by name
     *
     * @param attName - name of the attribute
     * @return attribute value
     * @throws ServiceException
     */
    String getAttribute(String attName) throws ServiceException;

    /**
     * Read all attributes of user with given name.
     *
     * @param userName - name of the user
     * @return list of user attributes
     * @throws ServiceException
     */
    List<ProfileAttribute> getAttributesOfUser(String userName) throws ServiceException;

    /**
     * Create new attribute.
     *
     * @param userName - name of the user
     * @param pa - attribute to create
     */
    void putAttribute(String userName, ProfileAttribute pa);
}
