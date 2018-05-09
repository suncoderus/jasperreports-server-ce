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
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.dto.resources.ClientMondrianXmlaDefinition;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableMondrianConnection;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class MondrianXmlaDefinitionResourceConverter extends ResourceConverterImpl<MondrianXMLADefinition, ClientMondrianXmlaDefinition> {
    @Resource(name = "resourceReferenceConverterProvider")
    private ResourceReferenceConverterProvider resourceConverterProvider;

    @Override
    protected MondrianXMLADefinition resourceSpecificFieldsToServer(ClientMondrianXmlaDefinition clientObject, MondrianXMLADefinition resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        resultToUpdate.setCatalog(clientObject.getCatalog());
        resultToUpdate.setMondrianConnection(resourceConverterProvider.getConverterForType(ClientReferenceableMondrianConnection.class).toServer(clientObject.getMondrianConnection(), resultToUpdate.getMondrianConnection(), options));
        return resultToUpdate;
    }

    @Override
    protected ClientMondrianXmlaDefinition resourceSpecificFieldsToClient(ClientMondrianXmlaDefinition client, MondrianXMLADefinition serverObject, ToClientConversionOptions options) {
        client.setCatalog(serverObject.getCatalog());
        client.setMondrianConnection(resourceConverterProvider.getConverterForType(ClientReferenceableMondrianConnection.class).toClient(serverObject.getMondrianConnection(), options));
        return client;
    }
}
