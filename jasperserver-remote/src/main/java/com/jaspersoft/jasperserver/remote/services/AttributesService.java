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

import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchCriteria;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchResult;
import com.jaspersoft.jasperserver.dto.authority.ClientAttribute;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.helpers.RecipientIdentity;

import java.util.List;
import java.util.Set;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public interface AttributesService {
    void deleteAttributes(RecipientIdentity holder, Set<String> attrNames) throws RemoteException;

    List<ClientAttribute> putAttributes(RecipientIdentity recipientIdentity, List<? extends ClientAttribute> newCollection,
                                            Set<String> names, boolean includeEffectivePermissions) throws RemoteException;

    AttributesSearchResult<ClientAttribute>
    getAttributes(AttributesSearchCriteria searchCriteria, boolean includeEffectivePermissions) throws RemoteException;

    List<ClientAttribute> getAttributes(RecipientIdentity holder, Set<String> names, boolean includeEffectivePermissions) throws RemoteException;

}
