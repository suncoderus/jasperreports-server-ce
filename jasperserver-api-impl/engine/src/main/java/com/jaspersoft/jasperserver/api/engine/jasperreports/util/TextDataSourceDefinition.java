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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomDomainMetaData;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import net.sf.jasperreports.data.csv.CsvDataAdapter;
import net.sf.jasperreports.data.csv.CsvDataAdapterImpl;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.design.JRDesignField;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ichan
 * CSV specific data source definition to create data connector (custom report data source) for CSV source
 * This class is meant to be instantiated as a spring bean that registers a custom data source with the system.
 */
public class TextDataSourceDefinition  extends AbstractTextDataSourceDefinition {

    private static String FILE_NAME_PROP = "fileName";
    public static String DATA_FILE_RESOURCE_ALIAS = "dataFile";
        // FOR METADATA DISCOVERY
    int rowCountForMetadataDiscovery = -1;      // number of rows to use for metadata discovery


    public TextDataSourceDefinition() {

        // define default values for the following properties
        Map<String, String> propertyDefaultValueMap = getPropertyDefaultValueMap();
        propertyDefaultValueMap.put("name", "csv");
        propertyDefaultValueMap.put("datePattern", "yyyy-MM-dd HH:mm:ss");
        propertyDefaultValueMap.put("queryExecuterMode", "true");
        propertyDefaultValueMap.put("useFirstRowAsHeader", "true");

        // hide the following properties from UI
        Set<String> hiddenPropertySet = getHiddenPropertySet();
        hiddenPropertySet.add("name");
        hiddenPropertySet.add("dataFile");
        hiddenPropertySet.add("queryExecuterMode");
        hiddenPropertySet.add("columnNames");
    }

    /*
     * This function is used for retrieving the metadata layer of the data connector in form of TableSourceMetadata
     * TableSourceMetadata contains information JRFields, query, query language and field name mapping (actual JRField name, name used in domain)
     *
     * NOTE:  Thread Repository Context needs to be set up properly before looking up csv file within repo
     */
    @Override
    public CustomDomainMetaData getCustomDomainMetaData(CustomReportDataSource customDataSource) throws Exception {

        // METADATA DISCOVERY
        Map<String, Object> propertyValueMap = new HashMap<String, Object>();
        propertyValueMap = getDataSourceServicePropertyMap(customDataSource, propertyValueMap);
        propertyValueMap = customizePropertyValueMap(customDataSource, propertyValueMap);

        // GET CSV DATA ADAPTER from Custom Report Data Source
        // map custom report data source properties to data adapter
        CsvDataAdapter csvDataAdapter = new CsvDataAdapterImpl();
        csvDataAdapter = (CsvDataAdapter) setupDataAdapter(csvDataAdapter, propertyValueMap);
        if (getValidator() != null) getValidator().validatePropertyValues(customDataSource, null);


        boolean useFirstRowAsHeader = (Boolean) propertyValueMap.get("useFirstRowAsHeader");
        boolean containsColumnNames = AbstractTextDataSourceDefinition.containsValue((List) propertyValueMap.get("columnNames"));
        List<String> fieldNames;
        Map<String, String> fieldMapping;
        JRCsvDataSource csvDataSource;

        if (!useFirstRowAsHeader && !containsColumnNames) {
            // force to use first row as header, so we can get the column count
            // then we can assign arbitrary names to columns
            csvDataAdapter.setUseFirstRowAsHeader(true);
            // get JRDataSource from data adapter
            csvDataSource = (JRCsvDataSource)getJRDataSource(csvDataAdapter);
            // get default JRField names
            fieldNames = getDefaultFieldNames(csvDataSource);
            if(fieldNames == null || fieldNames.isEmpty()){
                throw new JSException("Invalid field or column delimiter specified");
            }
            // create default name mapping <JRField Name, Display Name in Domain>
            fieldMapping = AbstractTextDataSourceDefinition.getDefaultFieldMapping(fieldNames);
            propertyValueMap.put("columnNames", fieldNames);
            csvDataSource.close();
            // create a new JRDataSource without using first row as header
            // therefore, we can detect the data type starting from the first row of data
            csvDataAdapter = (CsvDataAdapter) setupDataAdapter(csvDataAdapter, propertyValueMap);
            csvDataSource = (JRCsvDataSource)getJRDataSource(csvDataAdapter);
            csvDataSource.next();
        } else {
            // get JRDataSource from data adapter
            csvDataSource = (JRCsvDataSource)getJRDataSource(csvDataAdapter);
            // get default JRField names
            fieldNames = getFieldNames(csvDataSource);
            if(fieldNames == null || fieldNames.isEmpty()){
                throw new JSException("Invalid field or column delimiter specified");
            }
            // create default name mapping <JRField Name, Display Name in Domain>
            fieldMapping = AbstractTextDataSourceDefinition.getFieldMapping(fieldNames);
        }
        // create TableSourceMetadata object
        CustomDomainMetaDataImpl sourceMetadata = new CustomDomainMetaDataImpl();
        sourceMetadata.setQueryLanguage(getQueryLanguage());
        sourceMetadata.setFieldNames(fieldNames);
        sourceMetadata.setFieldMapping(fieldMapping);
        // set default column data type based on the actual data
        sourceMetadata.setFieldTypes(getFieldTypes(csvDataSource, sourceMetadata.getJRFieldList()));

        csvDataSource.close();
        return sourceMetadata;

    }

    @Override
    public Map<String, Object>  customizePropertyValueMap(CustomReportDataSource customReportDataSource, Map<String, Object>  propertyValueMap) {
        if(customReportDataSource.getResources() != null && customReportDataSource.getResources().get(DATA_FILE_RESOURCE_ALIAS) != null){
            // if data source has a data file as a sub resource, then take it's URI as file name property
            propertyValueMap.put(FILE_NAME_PROP, customReportDataSource.getResources()
                    .get(DATA_FILE_RESOURCE_ALIAS).getTargetURI());
        }
        return propertyValueMap;
    }

    // get JRField names from JRDataSource
    private List<String> getFieldNames(JRCsvDataSource csvDataSource) throws Exception {
        // SET COLUMN NAMES
        Map<String, Integer> columnNames;
        try {
            csvDataSource.next();
            columnNames = csvDataSource.getColumnNames();
        } catch (JRException e){
            throw new JSException("Unable to get field names. Please check if field/column delimiter is correct. "
                    + e.getMessage(), e);
        }
        if(columnNames == null || columnNames.isEmpty())return null;
        String columnNameArray[] = new String[columnNames.size()];
        for (Map.Entry<String, Integer> entry : columnNames.entrySet()) {
            isValidFieldName(entry.getKey());
            log.debug("KEY = " + entry.getKey() + ", VAL = " + entry.getValue());
            columnNameArray[entry.getValue()] = entry.getKey();
        }
        return Arrays.asList(columnNameArray);
    }

    protected String findType(JRDataSource csvDataSource, JRDesignField field) {
        String domainFieldType = super.findType(csvDataSource, field);
        if (domainFieldType != null && domainFieldType.equals("java.util.Date")) {
        /**
            String datePattern = null;
            if (((JRCsvDataSource)csvDataSource).getDateFormat() instanceof SimpleDateFormat)
                datePattern = ((SimpleDateFormat)((JRCsvDataSource)csvDataSource).getDateFormat()).toPattern();
            if (datePattern == null) return "java.sql.Timestamp";
            boolean containDate = (datePattern.indexOf("y") + datePattern.indexOf("M") + datePattern.indexOf("d") > -3);
            boolean containTime = (datePattern.indexOf("H") + datePattern.indexOf("m") + datePattern.indexOf("s") > -3);
            if (containDate && !containTime) return "java.sql.Date";
            if (containTime && !containDate) return "java.sql.Time";
        **/
            return "java.sql.Timestamp";
        }
        return domainFieldType;
    }

    /*
     * get arbitrary JRField names from JRDataSource (if header is not included in the data)
     * should be like COLUMN_1, COLUMN_2, COLUMN_3...
     */
    private List<String> getDefaultFieldNames(JRCsvDataSource csvDataSource) throws Exception {
        if (!csvDataSource.next()) return null;
        int fieldCount = 0;
        Map<String, Integer> columnNames = csvDataSource.getColumnNames();
        for (Map.Entry<String, Integer> entry : columnNames.entrySet()) {
            if (entry.getValue() >= fieldCount) fieldCount = entry.getValue() + 1;
        }
        List<String> fieldNames = new ArrayList<String>();
        for (int i = 0; i < fieldCount; i++) {
            fieldNames.add("COLUMN_" + i);
        }
        return fieldNames;
    }

}

