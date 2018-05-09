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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public interface ResourceConverterProvider {
    ToClientConverter<? super Resource, ? extends ClientResource, ToClientConversionOptions> getToClientConverter(String serverType) throws IllegalParameterValueException;

    ToClientConverter<? super Resource, ? extends ClientResource, ToClientConversionOptions> getToClientConverter(Resource serverObject);

    ToServerConverter<? super ClientResource, ? extends Resource, ToServerConversionOptions> getToServerConverter(ClientResource clientObject) throws IllegalParameterValueException;

    ToServerConverter<? super ClientResource, ? extends Resource, ToServerConversionOptions> getToServerConverter(String clientType) throws IllegalParameterValueException;

    ToClientConverter<? super Resource, ? extends ClientResource, ToClientConversionOptions> getToClientConverter(String serverType, String clientType);

    ToServerConverter<? super ClientResource, ? extends Resource, ToServerConversionOptions> getToServerConverter(String serverType, String clientType);

    Class<? extends ClientResource> getClientTypeClass(String clientType) throws IllegalParameterValueException;
}
