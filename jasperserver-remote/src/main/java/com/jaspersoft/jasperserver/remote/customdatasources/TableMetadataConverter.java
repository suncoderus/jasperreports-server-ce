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

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDomainMetaDataImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomDomainMetaData;
import com.jaspersoft.jasperserver.dto.connection.metadata.ColumnMetadata;
import com.jaspersoft.jasperserver.dto.connection.metadata.TableMetadata;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConverter;
import net.sf.jasperreports.engine.JRField;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class TableMetadataConverter implements ToServerConverter<TableMetadata, CustomDomainMetaDataImpl, ToServerConversionOptions>, ToClientConverter<CustomDomainMetaDataImpl, TableMetadata, ToClientConversionOptions> {
    @Override
    public TableMetadata toClient(CustomDomainMetaDataImpl serverObject, ToClientConversionOptions options) {
        final TableMetadata result = new TableMetadata();
        result.setQueryLanguage(serverObject.getQueryLanguage());
        final List<JRField> jrFieldList = serverObject.getJRFieldList();
        if (jrFieldList != null) {
            List<ColumnMetadata> columns = new ArrayList<ColumnMetadata>(jrFieldList.size());
            result.setColumns(columns);
            for (JRField field : jrFieldList) {
                columns.add(new ColumnMetadata()
                        .setName(field.getName())
                        .setJavaType(field.getValueClassName())
                        .setLabel(CustomDomainMetaDataImpl.getLabel(serverObject, field.getName())));
            }
        }
        return result;
    }

    @Override
    public String getClientResourceType() {
        return ClientTypeHelper.extractClientType(TableMetadata.class);
    }

    @Override
    public CustomDomainMetaDataImpl toServer(TableMetadata clientObject, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        return toServer(clientObject, new CustomDomainMetaDataImpl(), options);
    }

    @Override
    public CustomDomainMetaDataImpl toServer(TableMetadata clientObject, CustomDomainMetaDataImpl resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        resultToUpdate.setQueryLanguage(clientObject.getQueryLanguage());
        final List<String> fieldNames = new ArrayList<String>();
        final Map<String, String> fieldMapping = new HashMap<String, String>();
        final List<String> fieldTypes = new ArrayList<String>();
        if (clientObject.getColumns() != null) {
            for (ColumnMetadata column : clientObject.getColumns()) {
                fieldNames.add(column.getName());
                fieldTypes.add(column.getJavaType());
                fieldMapping.put(column.getName(), column.getLabel());
            }
        }
        resultToUpdate.setFieldMapping(fieldMapping.isEmpty() ? null : fieldMapping);
        resultToUpdate.setFieldNames(fieldNames.isEmpty() ? null : fieldNames);
        resultToUpdate.setFieldTypes(fieldTypes.isEmpty() ? null : fieldTypes);
        return resultToUpdate;
    }

    @Override
    public String getServerResourceType() {
        return CustomDomainMetaData.class.getName();
    }
}
