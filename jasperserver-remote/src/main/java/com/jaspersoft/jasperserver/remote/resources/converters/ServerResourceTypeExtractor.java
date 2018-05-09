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

import com.jaspersoft.jasperserver.war.helper.GenericParametersHelper;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ServerResourceTypeExtractor {
    private String serverResourceType;

    private final Class<? extends ToServerConverter> converterClass;

    public ServerResourceTypeExtractor(Class<? extends ToServerConverter> converterClass){
        this.converterClass = converterClass;
    }

    public String getServerResourceType() {
        if (serverResourceType == null) {
            final Class<?> serverResourceTypeClass = GenericParametersHelper.getGenericTypeArgument(converterClass,
                    ToServerConverter.class, 1);
            if (serverResourceTypeClass != null) {
                serverResourceType = serverResourceTypeClass.getName();
            } else {
                throw new IllegalStateException("Unable to identify serverResourceType. It can happen because " +
                        converterClass.getName() + " is raw implementation of " + ToServerConverter.class.getName());
            }
        }
        return serverResourceType;
    }
}
