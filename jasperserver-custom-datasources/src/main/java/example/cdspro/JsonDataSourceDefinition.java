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

package example.cdspro;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AbstractTextDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.design.JRDesignField;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomDomainMetaData;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;

/**
 * Defines JSON datasource which can be read from a file or repository.
 */
public class JsonDataSourceDefinition extends AbstractTextDataSourceDefinition {

    private static final long serialVersionUID = -1698557680384988822L;
    private RepositoryService repositoryService;

    @Override
    public CustomDomainMetaData getCustomDomainMetaData(CustomReportDataSource customDataSource) throws Exception {

        Map<String, Object> propertyValueMap = new HashMap<String, Object>();
        propertyValueMap = getDataSourceServicePropertyMap(customDataSource, propertyValueMap);

        List<JRField> jrFields = getFields(customDataSource, propertyValueMap);

        return CustomDomainMetadataUtils.createCustomDomainMetaData(getQueryLanguage(), jrFields);
    }

    private List<JRField> getFields(CustomReportDataSource customDataSource, Map<String, Object> propertyValueMap) throws JRException {
        String query = (String) propertyValueMap.get("selectExpression");
        ResourceReference resourceRef = null;
        if (customDataSource.getResources() != null) {
            resourceRef = customDataSource.getResources().get("dataFile");
        }

        JsonDataSourceUtils.RowExtractor rowExtractor = null;
        // if no resource then it's a file in a file system
        if (resourceRef == null) {
            rowExtractor = JsonDataSourceUtils.getRowExtractor((String) propertyValueMap.get("fileName"), query);
        } else {
            InputStream is = getResourceInputStream(resourceRef, repositoryService);
            rowExtractor = JsonDataSourceUtils.getRowExtractor(is, query);
        }
        List<JRField> jrFields = rowExtractor.getNextRowFields();
        for (int i = 0; (getRowCountForMetadataDiscovery() < 0) || (i < getRowCountForMetadataDiscovery()); i++) {
             List<JRField> newRowFields = rowExtractor.getNextRowFields();
            if (newRowFields == null) break;
            for (int j = 0; j < newRowFields.size(); j++) {
                String name = newRowFields.get(j).getName();
                JRField curField = findField(jrFields, name);
                if (curField == null) {
                    jrFields.add(newRowFields.get(j));
                } else {
                    ((JRDesignField)curField).setValueClassName(getCompatibleDataType(curField.getValueClassName(), newRowFields.get(j).getValueClassName()));
                }
            }
        }


        return jrFields;
    }

    private JRField findField(List<JRField>jrFields, String name) {
        for (JRField jrField: jrFields) {
            if (jrField.getName().equals(name)) return jrField;
        }
        return null;
    }


    private InputStream getJsonInputStream(FileResource fileResource) {
        InputStream is;
        if (fileResource.hasData()) {
            is = fileResource.getDataStream();
        } else {
            FileResourceData resourceData = repositoryService.getResourceData(null, fileResource.getURIString());
            is = resourceData.getDataStream();
        }
        return is;
    }

    static InputStream getResourceInputStream(ResourceReference resourceRef, RepositoryService repositoryService) {
        FileResource fileResource;
        InputStream is = null;
        if (resourceRef.isLocal()) {
            fileResource = (FileResource) resourceRef.getLocalResource();
        } else {
            fileResource = (FileResource) repositoryService.getResource(null, resourceRef.getReferenceURI(), FileResource.class);
        }
        if (fileResource != null) {
            if (fileResource.hasData()) {
                is = fileResource.getDataStream();
            } else {
                FileResourceData resourceData = repositoryService.getResourceData(null, fileResource.getURIString());
                is = resourceData.getDataStream();
            }
        } else {
            ContentResource contentResource = (ContentResource) repositoryService.getResource(null, resourceRef.getReferenceURI(), ContentResource.class);
            if (contentResource.hasData()) {
                is = contentResource.getDataStream();
            } else {
                FileResourceData resourceData = repositoryService.getContentResourceData(null, contentResource.getURIString());
                is = resourceData.getDataStream();
            }
        }
        return is;
    }

    @Override
    public Map<String, Object> customizePropertyValueMap(CustomReportDataSource customReportDataSource, Map<String, Object> propertyValueMap) {
        return propertyValueMap;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

}
