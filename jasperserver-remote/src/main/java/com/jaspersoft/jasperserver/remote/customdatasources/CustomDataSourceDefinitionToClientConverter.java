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
package com.jaspersoft.jasperserver.remote.customdatasources;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.CustomReportDataSourceService;
import com.jaspersoft.jasperserver.dto.customdatasources.ClientCustomDataSourceDefinition;
import com.jaspersoft.jasperserver.dto.customdatasources.CustomDataSourcePropertyDefinition;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class CustomDataSourceDefinitionToClientConverter implements ToClientConverter<CustomDataSourceDefinition, ClientCustomDataSourceDefinition, ToClientConversionOptions> {
    @Override
    public ClientCustomDataSourceDefinition toClient(CustomDataSourceDefinition serverObject, ToClientConversionOptions options) {
        final ClientCustomDataSourceDefinition clientDefinition = new ClientCustomDataSourceDefinition().setName(serverObject.getName());
        if (serverObject.getPropertyDefinitions() != null) {
            final List<CustomDataSourcePropertyDefinition> propertyDefinitions = new ArrayList<CustomDataSourcePropertyDefinition>();
            for (Map<String, ?> currentDefinition : serverObject.getPropertyDefinitions()) {
                final Object hidden = currentDefinition.get(CustomDataSourceDefinition.PARAM_HIDDEN);
                if (hidden == null || (hidden instanceof String && !Boolean.valueOf((String) hidden))) {
                    final CustomDataSourcePropertyDefinition currentPropertyDefinition = new CustomDataSourcePropertyDefinition()
                            .setName((String) currentDefinition.get(CustomDataSourceDefinition.PARAM_NAME))
                            .setDefaultValue((String) currentDefinition.get(CustomDataSourceDefinition.PARAM_DEFAULT))
                            .setLabel((String) currentDefinition.get(CustomDataSourceDefinition.PARAM_LABEL));
                    final ArrayList<ClientProperty> properties = new ArrayList<ClientProperty>();
                    for (String currentParameter : currentDefinition.keySet()) {
                        if (!(CustomDataSourceDefinition.PARAM_NAME.equals(currentParameter)
                                || CustomDataSourceDefinition.PARAM_DEFAULT.equals(currentParameter)
                                || CustomDataSourceDefinition.PARAM_LABEL.equals(currentParameter))) {
                            properties.add(new ClientProperty(currentParameter, (String) currentDefinition.get(currentParameter)));
                        }
                    }
                    if (!properties.isEmpty()) currentPropertyDefinition.setProperties(properties);
                    propertyDefinitions.add(currentPropertyDefinition);
                }
            }
            if (!propertyDefinitions.isEmpty()) clientDefinition.setPropertyDefinitions(propertyDefinitions);
        }
        if (serverObject.getQueryExecuterMap() != null && !serverObject.getQueryExecuterMap().isEmpty()) {
            clientDefinition.setQueryTypes(new ArrayList<String>(serverObject.getQueryExecuterMap().keySet()));
        }
        try {
            Class<?> serviceClass = Class.forName(serverObject.getServiceClassName());
            // Instance created to evaluate if it implements the test interface
            Object newServiceClass = serviceClass.newInstance();
            clientDefinition.setTestable(newServiceClass instanceof CustomReportDataSourceService);
        } catch (Exception e) {
            // can't get service class instance here. Then it's not testable
        }
        return clientDefinition;
    }

    @Override
    public String getClientResourceType() {
        return ClientTypeHelper.extractClientType(ClientCustomDataSourceDefinition.class);
    }
}
