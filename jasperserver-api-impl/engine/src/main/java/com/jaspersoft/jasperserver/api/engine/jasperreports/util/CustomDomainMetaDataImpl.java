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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;


import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomDomainMetaData;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.design.JRDesignField;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ichan
 * This class contains the metadata layer for connector which contains JRField names, field types, query language,
 * query text, description, and field name mapping <JRField name, Name in domain>
 */
@JasperServerAPI
public class CustomDomainMetaDataImpl implements CustomDomainMetaData {

    private List<String> fieldNames;    // JRField Names
    private Map<String, String> fieldMapping;  // <JRField Name, Domain Name>
    private List<String> fieldTypes;    // field type
    private List<String> fieldDescriptions;    // field description
    private String queryLanguage;   // query language
    private String queryText;       // query text
    List<JRField> baseFieldList = null;

    /**
     *  return field names
     */
    public List<String> getFieldNames() {
        return fieldNames;
    }

    /**
     *  set field names
     */
    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
        baseFieldList = null;
    }

    /**
     *  return field mapping relationship between data source field names and domain display names
     */
    public Map<String, String> getFieldMapping() {
        return fieldMapping;
    }

    /**
     *  set field mapping relationship between data source field names and domain display names
     */
    public void setFieldMapping(Map<String, String> fieldMapping) {
        this.fieldMapping = fieldMapping;
    }

    /**
     *  get field types
     */
    public List<String> getFieldTypes() {
        return fieldTypes;
    }

    /**
     *  set field types
     */
    public void setFieldTypes(List<String> fieldTypes) {
        this.fieldTypes = fieldTypes;
        baseFieldList = null;
    }

    /**
     *  get field descriptions
     */
    public List<String> getFieldDescriptions() {
        return fieldDescriptions;
    }

    /**
     *  set field descriptions
     */
    public void setFieldDescriptions(List<String> fieldDescriptions) {
        this.fieldDescriptions = fieldDescriptions;
        baseFieldList = null;
    }

    /**
     *  set query languages
     */
    public String getQueryLanguage() {
        return queryLanguage;
    }

    /**
     *  return query languages
     */
    public void setQueryLanguage(String queryLanguage) {
        this.queryLanguage = queryLanguage;
    }

    /**
     *  return query that uses in query executer
     */
    public String getQueryText() {
        return queryText;
    }

    /**
     *  set query that uses in query executer
     */
    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }


    /**
     *  return list of JRField Name (name, type, description) for custom data source
     */
    public List<JRField> getJRFieldList() {
        if (baseFieldList != null) return baseFieldList;
        baseFieldList = new ArrayList<JRField>();
        if(fieldNames != null) {
            for (int i = 0; i < fieldNames.size(); i++) {
                JRDesignField jrf = new JRDesignField();
                jrf.setName(fieldNames.get(i));
                jrf.setValueClassName((String) getItem(fieldTypes, i, "java.lang.String"));
                jrf.setDescription((String) getItem(fieldDescriptions, i, null));
                baseFieldList.add(jrf);
            }
        }
        return baseFieldList;
    }

    Object getItem(List list, int index, Object defValue) {
        if ((list == null) || (index >= list.size())) return defValue;
        return list.get(index);
    }

    public static String getLabel(CustomDomainMetaData customDomainMetaData, String fieldName) {
        if ((customDomainMetaData != null) && (customDomainMetaData.getFieldMapping() != null)) {
            String label = customDomainMetaData.getFieldMapping().get(fieldName);
            if (label != null) return label;
        }
        return fieldName;
    }

    public static String getDescription(CustomDomainMetaData customDomainMetaData, String fieldName) {
        if ((customDomainMetaData != null) && (customDomainMetaData.getFieldMapping() != null)) {
            for (JRField field : customDomainMetaData.getJRFieldList()) {
                if (field.getName().equals(fieldName)) {
                    return field.getDescription();
                }
            }
        }
        return null;
    }

}
