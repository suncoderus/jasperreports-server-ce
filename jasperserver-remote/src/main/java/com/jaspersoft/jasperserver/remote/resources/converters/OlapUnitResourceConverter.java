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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.dto.resources.ClientOlapUnit;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenciableOlapConnection;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author vsabadosh
 */
@Service
public class OlapUnitResourceConverter extends ResourceConverterImpl<OlapUnit, ClientOlapUnit> {
    @javax.annotation.Resource
    protected ResourceReferenceConverterProvider resourceReferenceConverterProvider;
    @Override
    protected OlapUnit resourceSpecificFieldsToServer(ClientOlapUnit clientObject, OlapUnit resultToUpdate,
            ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        resultToUpdate.setMdxQuery(clientObject.getMdxQuery());
        final ResourceReference serverOlapConnection = resourceReferenceConverterProvider
                .getConverterForType(ClientReferenciableOlapConnection.class)
                .toServer(clientObject.getOlapConnection(), resultToUpdate.getOlapClientConnection(), options);
        resultToUpdate.setOlapClientConnection(serverOlapConnection);
        return resultToUpdate;
    }

    @Override
    protected ClientOlapUnit resourceSpecificFieldsToClient(ClientOlapUnit client, OlapUnit serverObject,
            ToClientConversionOptions options) {
        client.setMdxQuery(serverObject.getMdxQuery());
        final ClientReferenciableOlapConnection clientOlapConnection = resourceReferenceConverterProvider
                .getConverterForType(ClientReferenciableOlapConnection.class)
                .toClient(serverObject.getOlapClientConnection(), options);
        client.setOlapConnection(clientOlapConnection);
        return client;
    }
}
