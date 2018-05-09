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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 * @version $Id: $
 */
public class ClientResourceResolver {
    //Maps label of resource type to actual java resource types. This map should be replaced in future with instance of
    // <class>ResourceConverterProvider</class>.
    private Map<String, Set<Class<? extends Resource>>> clientResourceTypeToServerResourceTypes;

    public Set<Class<? extends Resource>> getResourceTypes(Set<String> resourceTypeLabels) {
        Set<Class<? extends Resource>> result = new HashSet<Class<? extends Resource>>();
        for (String clientResourceLabel : resourceTypeLabels) {
            Set serverResourceTypes = clientResourceTypeToServerResourceTypes.get(clientResourceLabel);
            if (serverResourceTypes != null) {
                result.addAll(serverResourceTypes);
            }
        }

        return result;
    }

    public void setClientResourceTypeToServerResourceTypes(Map<String, Set<Class<? extends Resource>>> clientResourceTypeToServerResourceTypes) {
        this.clientResourceTypeToServerResourceTypes = clientResourceTypeToServerResourceTypes;
    }

    public Map<String, Set<Class<? extends Resource>>> getClientResourceTypeToServerResourceTypes() {
        return clientResourceTypeToServerResourceTypes;
    }
}
