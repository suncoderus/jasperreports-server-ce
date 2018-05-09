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
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.dto.authority.ClientRole;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Service
public class RoleConverter implements ToServerConverter<ClientRole, Role, ToServerConversionOptions>, ToClientConverter<Role, ClientRole, ToClientConversionOptions> {
    @Override
    public ClientRole toClient(Role serverObject, ToClientConversionOptions options) {
        ClientRole clientRole = new ClientRole();

        clientRole.setName(serverObject.getRoleName());
        clientRole.setExternallyDefined(serverObject.isExternallyDefined());
        clientRole.setTenantId(serverObject.getTenantId());

        return clientRole;
    }

    @Override
    public String getClientResourceType() {
        return ClientRole.class.getName();
    }

    @Override
    public Role toServer(ClientRole clientObject, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        return toServer(clientObject, new RoleImpl(), null);
    }

    @Override
    public Role toServer(ClientRole clientObject, Role resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        resultToUpdate.setRoleName(clientObject.getName());
        resultToUpdate.setTenantId(clientObject.getTenantId());
        resultToUpdate.setExternallyDefined(clientObject.isExternallyDefined());

        return resultToUpdate;
    }

    @Override
    public String getServerResourceType() {
        return Role.class.getName();
    }
}
