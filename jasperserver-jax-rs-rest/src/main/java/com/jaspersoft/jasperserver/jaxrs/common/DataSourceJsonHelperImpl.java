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
package com.jaspersoft.jasperserver.jaxrs.common;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import com.jaspersoft.jasperserver.war.action.DataSourceJsonHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class DataSourceJsonHelperImpl implements DataSourceJsonHelper {
    @Resource
    private JacksonMapperContextResolver jacksonMapperContextResolver;
    @Resource
    private ResourceConverterProvider resourceConverterProvider;

    @Override
    public ReportDataSource parse(String dataSourceJson, String clientType) {
        try {
            final Class<? extends ClientResource> clientTypeClass = resourceConverterProvider
                    .getClientTypeClass(clientType);
            final ClientResource clientResource = jacksonMapperContextResolver.getContext(clientTypeClass)
                    .reader(clientTypeClass).readValue(dataSourceJson);
            return (ReportDataSource) resourceConverterProvider.getToServerConverter(clientResource)
                    .toServer(clientResource, ToServerConversionOptions.getDefault());
        } catch (Exception e) {
            throw new JSExceptionWrapper(e);
        }
    }
}
