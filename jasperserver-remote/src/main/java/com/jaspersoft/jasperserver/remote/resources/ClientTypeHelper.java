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
package com.jaspersoft.jasperserver.remote.resources;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.war.helper.GenericParametersHelper;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ClientTypeHelper<T> {

    private final Class<? extends ToClientConverter<?, T, ?>> converterClass;

    public ClientTypeHelper(Class<? extends ToClientConverter> converterClass){
        this.converterClass = (Class)converterClass;
    }

    public ClientTypeHelper(ToClientConverter<?, T, ?> converter){
        this.converterClass = (Class)converter.getClass();
    }

    private Class<?> clientClass;
    private String clientResourceType;

    public Class<T> getClientClass(){
        if(clientClass == null){
            clientClass = (Class) GenericParametersHelper.getGenericTypeArgument(converterClass, ToClientConverter.class, 1);
            if (clientClass == null) {
                throw new IllegalStateException("Unable to identify clientTypeClass. It can happen because " +
                        converterClass.getName() + " is raw implementation of " + ToClientConverter.class.getName());
            }
        }
        return (Class<T>) clientClass;
    }

    public T getNewClientObjectInstance() {
        try {
            return (T) getClientClass().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Couldn't instantiate client object", e);
        }
    }

    public String getClientResourceType() {
        if (clientResourceType == null) {
            clientResourceType = ClientTypeHelper.extractClientType(getClientClass());
        }
        return clientResourceType;
    }

    public static String extractClientType(Class<?> clientObjectClass) {
        String clientResourceType = null;
        final XmlRootElement xmlRootElement = clientObjectClass.getAnnotation(XmlRootElement.class);
        if (xmlRootElement != null && !"##default".equals(xmlRootElement.name())) {
            clientResourceType = xmlRootElement.name();
        } else {
            final XmlType xmlType = clientObjectClass.getAnnotation(XmlType.class);
            if (xmlType != null && !"##default".equals(xmlType.name())) {
                clientResourceType = xmlType.name();
            }
        }
        if (clientResourceType == null) {
            final String classSimpleName = clientObjectClass.getSimpleName();
            clientResourceType = classSimpleName.replaceFirst("^.", classSimpleName.substring(0, 1).toLowerCase());
        }
        return clientResourceType;
    }
}
