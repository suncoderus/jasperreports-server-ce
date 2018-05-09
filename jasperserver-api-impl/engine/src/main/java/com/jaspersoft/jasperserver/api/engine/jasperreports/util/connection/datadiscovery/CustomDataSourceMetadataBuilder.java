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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util.connection.datadiscovery;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AbstractTextDataSourceDefinition;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDomainMetaDataImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.JsonMarshaller;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomDomainMetaData;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationSingleElement;
import net.sf.jasperreports.engine.JRField;
import java.util.ArrayList;
import java.util.Map;

public class CustomDataSourceMetadataBuilder{

    public static String METADATA_FILE_RESOURCE_ALIAS = "jsonmetadatafile";

    public static String metadataToJsonContent(CustomDomainMetaData customDomainMetaData, Map<String, String[]> options, JsonMarshaller jsonMarshaller) {
        PresentationGroupElement schemaElement = CustomDataSourceMetadataBuilder.build(customDomainMetaData, null);
        return jsonMarshaller.toJson(schemaElement);
    }

    public static PresentationGroupElement build(CustomDomainMetaData customDomainMetaData, Map<String, String[]> options) {
        final ArrayList<PresentationElement> elements = new ArrayList<PresentationElement>(customDomainMetaData.getJRFieldList().size());
        for (JRField jrField: customDomainMetaData.getJRFieldList()) {
            elements.add(new PresentationSingleElement()
                    .setDescription(jrField.getDescription())
                    .setType(jrField.getValueClassName())
                    .setName(jrField.getName()));
        }
        return new PresentationGroupElement().setElements(elements);
    }



    public static CustomDomainMetaData jsonContentToMetadata(String jsonContent, String queryLanguage,   Map<String, String[]> options, JsonMarshaller jsonMarshaller) {
        PresentationGroupElement presentationGroupElement = jsonMarshaller.fromJson(jsonContent, PresentationGroupElement.class);

        ArrayList<String> metaDataFieldNames = new ArrayList<String>();
        ArrayList<String> metaDataFieldTypes = new ArrayList<String>();
        ArrayList<String> metaDataFieldDescs = new ArrayList<String>();

        for (PresentationElement presentationElement : presentationGroupElement.getElements()) {
            metaDataFieldNames.add(presentationElement.getName());
            metaDataFieldTypes.add(((PresentationSingleElement) presentationElement).getType());
            metaDataFieldDescs.add(presentationElement.getDescription());
        }
        Map<String, String> fieldMapping = AbstractTextDataSourceDefinition.getFieldMapping(metaDataFieldNames);
        // create TableSourceMetadata object
        CustomDomainMetaDataImpl sourceMetadata = new CustomDomainMetaDataImpl();
        sourceMetadata.setQueryLanguage(queryLanguage);
        sourceMetadata.setFieldNames(metaDataFieldNames);
        if (!containsAllNulls(metaDataFieldDescs)) sourceMetadata.setFieldDescriptions(metaDataFieldDescs);
        sourceMetadata.setFieldMapping(fieldMapping);
        // set default column data type based on the actual data
        sourceMetadata.setFieldTypes(metaDataFieldTypes);
        return sourceMetadata;
    }

    public static String getJsonMetadataContentFromDataSource(CustomReportDataSource customReportDataSource, RepositoryService repository) {
        ResourceReference resourceReference = customReportDataSource.getResources().get(METADATA_FILE_RESOURCE_ALIAS);
        FileResource fileResource = null;
        if(resourceReference == null) {
            return null;
        }
        if (resourceReference.isLocal()) {
            fileResource = (FileResource) resourceReference.getLocalResource();
        } else {
            fileResource = (FileResource) repository.getResource(null, resourceReference.getReferenceURI(), com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource.class);
        }
        if (fileResource.hasData()) {
            return new String(fileResource.getData());
        } else {
            FileResourceData resourceData = repository.getResourceData(null, fileResource.getURIString());
            return new String(resourceData.getData());
        }
    }

    public static boolean containsMetadata(CustomReportDataSource customReportDataSource) {
        return ((customReportDataSource.getResources() != null) && (customReportDataSource.getResources().get(METADATA_FILE_RESOURCE_ALIAS) != null));
    }

    public static boolean containsAllNulls(ArrayList arrayList) {
        for(Object item : arrayList) if(item != null) return false;
        return true;
    }

}
