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
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientSubDataSourceReference;
import com.jaspersoft.jasperserver.dto.resources.ClientVirtualDataSource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class VirtualDataSourceResourceConverter extends ResourceConverterImpl<VirtualReportDataSource, ClientVirtualDataSource> {
    @javax.annotation.Resource(name = "concreteRepository")
    protected RepositoryService repositoryService;

    @Override
    protected VirtualReportDataSource resourceSpecificFieldsToServer(ClientVirtualDataSource clientObject, VirtualReportDataSource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        final List<ClientSubDataSourceReference> subDataSources = clientObject.getSubDataSources();
        Map<String,ResourceReference> dataSourceUriMap = Collections.EMPTY_MAP;
        if(subDataSources != null && !subDataSources.isEmpty()){
            dataSourceUriMap = new HashMap<String, ResourceReference>(subDataSources.size());
            for(ClientSubDataSourceReference currentReference : subDataSources){
                dataSourceUriMap.put(currentReference.getId(), new ResourceReference(currentReference.getUri()));
            }
        }

        resultToUpdate.setDataSourceUriMap(dataSourceUriMap);
        return resultToUpdate;
    }

    @Override
    protected ClientVirtualDataSource resourceSpecificFieldsToClient(ClientVirtualDataSource client, VirtualReportDataSource serverObject, ToClientConversionOptions options) {
        final Map<String,ResourceReference> dataSourceUriMap = serverObject.getDataSourceUriMap();
        List<ClientSubDataSourceReference> subDataSources = null;
        if(dataSourceUriMap != null && !dataSourceUriMap.isEmpty()){
            subDataSources = new ArrayList<ClientSubDataSourceReference>(dataSourceUriMap.size());
            for(String currentId : dataSourceUriMap.keySet()){
                final ClientSubDataSourceReference currentReference = new ClientSubDataSourceReference();
                currentReference.setId(currentId);
                currentReference.setUri(dataSourceUriMap.get(currentId).getReferenceURI());
                subDataSources.add(currentReference);
            }
        }
        client.setSubDataSources(subDataSources);
        return client;
    }
}
