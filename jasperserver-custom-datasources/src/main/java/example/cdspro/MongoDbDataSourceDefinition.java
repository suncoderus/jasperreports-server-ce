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

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomDomainMetaData;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.DataAdapterDefinition;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.DataAdapterDefinitionUtil;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDomainMetaDataImpl;
import com.jaspersoft.mongodb.MongoDbFieldsProvider;
import com.jaspersoft.mongodb.adapter.MongoDbDataAdapter;
import com.jaspersoft.mongodb.adapter.MongoDbDataAdapterImpl;
import com.jaspersoft.mongodb.connection.MongoDbConnection;
import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author ichan
 * custom report data source definition for MongoDB Query
 * This class is meant to be instantiated as a spring bean that registers a custom data source with the system.
 */
public class MongoDbDataSourceDefinition extends DataAdapterDefinition {

    public MongoDbDataSourceDefinition() {
        // add additional field
        Set<String> additionalPropertySet = getAdditionalPropertySet();
        additionalPropertySet.add("query");

        // define default values for the following properties
        Map<String, String> propertyDefaultValueMap = getPropertyDefaultValueMap();
        propertyDefaultValueMap.put("name", "mongoDbWithMetaData");
        propertyDefaultValueMap.put("queryExecuterMode", "true");
        propertyDefaultValueMap.put("query", "");

        // hide the following properties from UI
        Set<String> hiddenPropertySet = getHiddenPropertySet();
        hiddenPropertySet.add("name");
        hiddenPropertySet.add("queryExecuterMode");
        hiddenPropertySet.add("columnNames");
        // set query executor factory
        Map<String, String> queryExecuterMap = new HashMap<String, String>();
        queryExecuterMap.put("MongoDbQuery", "com.jaspersoft.mongodb.query.MongoDbQueryExecuterFactory");
        setQueryExecuterMap(queryExecuterMap);
    }

    @Override
    public Map<String, Object>  customizePropertyValueMap(CustomReportDataSource customReportDataSource, Map<String, Object>  propertyValueMap) {
        if (propertyValueMap.get("mongoURI") != null) {
            propertyValueMap.put("mongoURI", ((String) propertyValueMap.get("mongoURI")).trim());
        }
        return propertyValueMap;
    }

    /*
    * This function is used for retrieving the metadata layer of the custom data source in form of CustomDomainMetaData
    * CustomDomainMetaData contains information JRFields, query, query language and field name mapping (actual JRField name, name used in domain)
    */
    @Override
    public CustomDomainMetaData getCustomDomainMetaData(CustomReportDataSource customDataSource) throws Exception {

        // METADATA DISCOVERY
        Map<String, Object> propertyValueMap = new HashMap<String, Object>();
        propertyValueMap = getDataSourceServicePropertyMap(customDataSource, propertyValueMap);
        propertyValueMap = customizePropertyValueMap(customDataSource, propertyValueMap);

        // create MongoDB DATA ADAPTER from Custom Report Data Source properties
        // map custom report data source properties to data adapter
        MongoDbDataAdapter mongoDbDataAdapter = new MongoDbDataAdapterImpl();
        mongoDbDataAdapter = (MongoDbDataAdapter) setupDataAdapter(mongoDbDataAdapter, propertyValueMap);

        if (getValidator() != null) getValidator().validatePropertyValues(customDataSource, null);
        String query = (String)propertyValueMap.get("query");
        java.util.List<net.sf.jasperreports.engine.design.JRDesignField> jrDesignFields = getJRDesignFields(mongoDbDataAdapter, query);

        List<String> columnNames = new ArrayList<String>();
        List<String> columnTypes = new ArrayList<String>();
        List<String> columnDescriptions = new ArrayList<String>();
        for (JRDesignField field : jrDesignFields) {
            String designName = field.getName().replace(".", "_");
            columnNames.add(designName);
            if (isSupportedType(field.getValueClassName())) columnTypes.add(getFieldType(field.getValueClassName()));
            else columnTypes.add("java.lang.String");    // if it is not supported types, converts it to string for now
            columnDescriptions.add(field.getName());
        }
        // create CustomDomainMetaDataImpl object
        CustomDomainMetaDataImpl sourceMetadata = new CustomDomainMetaDataImpl();
        sourceMetadata.setQueryLanguage("MongoDbQuery");
        sourceMetadata.setFieldNames(columnNames);
        Map<String, String> fieldMapping = new HashMap<String, String>();
        for (String str : columnNames) fieldMapping.put(str, str);
        sourceMetadata.setFieldMapping(fieldMapping);
        // set default column data type based on the actual data
        sourceMetadata.setFieldTypes(columnTypes);
        sourceMetadata.setQueryText(query);
        sourceMetadata.setFieldDescriptions(columnDescriptions);

        return sourceMetadata;

    }

    /**
	*  Return data adapter service for this custom data source
	**/	
    @Override
    public DataAdapterService getDataAdapterService(JasperReportsContext jasperReportsContext, DataAdapter dataAdapter) {
            return new MongoDbDataAdapterService(jasperReportsContext, (MongoDbDataAdapter)dataAdapter);
    }

    protected java.util.List<net.sf.jasperreports.engine.design.JRDesignField> getJRDesignFields(MongoDbDataAdapter dataAdapter, String query) throws Exception {
            Map<String,Object> parameterValues = new HashMap<String, Object>();
            // convert your param map to fill params...
            Map<String, Object>  fillParams = DataAdapterDefinitionUtil.convertToFillParameters(parameterValues, getQueryExecuterFactory().getBuiltinParameters());
            MongoDbConnection mongoDbConnection = new MongoDbConnection(dataAdapter.getMongoURI(), dataAdapter.getUsername(), dataAdapter.getPassword());
            JRDesignDataset designDataset = new JRDesignDataset(false);
            JRDesignQuery jrquery = new JRDesignQuery();
            jrquery.setText(query);
            jrquery.setLanguage("MongoDbQuery");
            designDataset.setQuery(jrquery);
            return MongoDbFieldsProvider.getInstance().getFields(getJasperReportsContext(), designDataset, fillParams, mongoDbConnection);
    }

    private static Map typeMap;
    private static Set supportedTypeSet;
    static {
        supportedTypeSet = new HashSet();
        supportedTypeSet.add("java.lang.String");
        supportedTypeSet.add("java.lang.Byte");
        supportedTypeSet.add("java.lang.Short");
        supportedTypeSet.add("java.lang.Integer");
        supportedTypeSet.add("java.lang.Long");
        supportedTypeSet.add("java.lang.Float");
        supportedTypeSet.add("java.lang.Double");
        supportedTypeSet.add("java.lang.Number");
        supportedTypeSet.add("java.util.Date");
        supportedTypeSet.add("java.sql.Date");
        supportedTypeSet.add("java.sql.Time");
        supportedTypeSet.add("java.sql.Timestamp");
        supportedTypeSet.add("java.math.BigDecimal");
        supportedTypeSet.add("java.math.BigInteger");
        supportedTypeSet.add("java.lang.Boolean");
        supportedTypeSet.add("java.lang.Object");
        typeMap = new HashMap();
        typeMap.put("java.util.Date", "java.sql.Timestamp");
    }

    private boolean isSupportedType(String type) {
        return supportedTypeSet.contains(type);
    }

    private String getFieldType(String originalType) {
        Object mapType = typeMap.get(originalType);
        return (mapType != null?  (String)mapType : originalType);
    }

}

