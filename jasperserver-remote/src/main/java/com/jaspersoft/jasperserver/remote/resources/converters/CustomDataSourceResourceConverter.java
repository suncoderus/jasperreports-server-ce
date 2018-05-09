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

import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientCustomDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableFile;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class CustomDataSourceResourceConverter extends ResourceConverterImpl<CustomReportDataSource, ClientCustomDataSource> {
    @Resource(name = "cdsPropertiesToIgnore")
    protected Set<String> propertiesToIgnore;
    @Resource(name = "customDataSourceServiceFactory")
    private CustomReportDataSourceServiceFactory customDataSourceFactory;
    @Resource
    protected ResourceReferenceConverterProvider resourceReferenceConverterProvider;

    @Override
    protected CustomReportDataSource resourceSpecificFieldsToServer(ClientCustomDataSource clientObject, CustomReportDataSource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        resultToUpdate.setDataSourceName(clientObject.getDataSourceName());
        final CustomDataSourceDefinition definition = customDataSourceFactory.getDefinitionByName(clientObject.getDataSourceName());
        // let's fill service class by dataSourceName. If dataSourceName is incorrect and no such definition,
        // then let validator throw corresponding error
        if (definition != null) {
            // service class attribute is read only. It's determined by dataSourceName
            resultToUpdate.setServiceClass(definition.getServiceClassName());
        }
        Map<String, Object> properties = new HashMap<String, Object>();
        final Map resultToUpdatePropertyMap = resultToUpdate.getPropertyMap();
        if (resultToUpdatePropertyMap != null && !resultToUpdatePropertyMap.isEmpty()) {
            // put all properties to ignore (if any exists) to the result properties map
            for (String currentPropertyToIgnore : propertiesToIgnore) {
                if (resultToUpdatePropertyMap.containsKey(currentPropertyToIgnore)) {
                    properties.put(currentPropertyToIgnore, resultToUpdatePropertyMap.get(currentPropertyToIgnore));
                }
            }
        }
        // put all the properties, received from the client
        final List<ClientProperty> clientProperties = clientObject.getProperties();
        if (clientProperties != null && !clientProperties.isEmpty()) {
            for (ClientProperty property : clientProperties) {
                final String propertyKey = property.getKey();
                final String propertyValue = property.getValue();
                if ((propertiesToIgnore.contains(propertyKey) && propertyValue != null)
                        || !propertiesToIgnore.contains(propertyValue)) {
                    // put current property if not ignored property or property is ignored but isn't null (overwrite)
                    properties.put(propertyKey, propertyValue);
                }
            }
        }
        resultToUpdate.setPropertyMap(properties.isEmpty() ? null : properties);
        resultToUpdate.setResources(convertResourcesToServer(clientObject.getResources(), options));
        return resultToUpdate;
    }

    protected Map<String, ResourceReference> convertResourcesToServer(Map<String, ClientReferenceableFile> resources, ToServerConversionOptions options){
        Map<String, ResourceReference> serverResources = null;
        if(resources != null && !resources.isEmpty()){
            serverResources = new HashMap<String, ResourceReference>();
            final ResourceReferenceConverter<ClientReferenceableFile> referenceConverter =
                    resourceReferenceConverterProvider.getConverterForType(ClientReferenceableFile.class);
            for(String key : resources.keySet()){
                serverResources.put(key, referenceConverter.toServer(resources.get(key), options));
            }
        }
        return serverResources;
    }

    protected Map<String, ClientReferenceableFile> convertResourcesToClient(Map<String, ResourceReference> serverResources, ToClientConversionOptions options){
        Map<String, ClientReferenceableFile> resources = null;
        if(serverResources != null && !serverResources.isEmpty()){
            resources = new HashMap<String, ClientReferenceableFile>(serverResources.size());
            final ResourceReferenceConverter<ClientReferenceableFile> referenceConverter =
                    resourceReferenceConverterProvider.getConverterForType(ClientReferenceableFile.class);
            for(String key : serverResources.keySet()){
                resources.put(key, referenceConverter.toClient(serverResources.get(key), options));
            }
        }
        return resources;
    }

    @Override
    protected ClientCustomDataSource resourceSpecificFieldsToClient(ClientCustomDataSource client, CustomReportDataSource serverObject, ToClientConversionOptions options) {
        client.setDataSourceName(serverObject.getDataSourceName());
        client.setServiceClass(serverObject.getServiceClass());
        List<ClientProperty> properties = null;
        final Map propertyMap = serverObject.getPropertyMap();
        if(propertyMap != null && !propertyMap.isEmpty()){
            properties = new ArrayList<ClientProperty>(propertyMap.size());
            final Set<String> set = propertyMap.keySet();
            for(String key : set){
                if (!propertiesToIgnore.contains(key)) {
                    properties.add(new ClientProperty(key, (String)propertyMap.get(key)));
                }
            }
        }
        client.setProperties(properties == null || properties.isEmpty() ? null : properties);
        client.setResources(convertResourcesToClient(serverObject.getResources(), options));
        return client;
    }
}
