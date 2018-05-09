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

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomDomainMetaData;
import java.util.Map;
import java.util.Set;


/**
 * @author ichan
 * custom report data source definition for SPARK QUERY
 * This class is meant to be instantiated as a spring bean that registers a custom data source with the system.
 */
public class MongoDbJDBCDataSourceDefinition extends JDBCQueryDataSourceDefinition {

    public static String FILE_NAME_PROP = "fileName";
    public static String DATA_FILE_RESOURCE_ALIAS = "dataFile";

    public MongoDbJDBCDataSourceDefinition() {
        super();
        // add additional field
        Set<String> additionalPropertySet = getAdditionalPropertySet();
        additionalPropertySet.remove("query");
        additionalPropertySet.add("portNumber");
        additionalPropertySet.add("connectionOptions");
        additionalPropertySet.add("timeZone");
        additionalPropertySet.add(FILE_NAME_PROP);

        // define default values for the following properties
        Map<String, String> propertyDefaultValueMap = getPropertyDefaultValueMap();
        propertyDefaultValueMap.remove("query");
        propertyDefaultValueMap.put("name", "MongoDbJDBCDataSource");
        propertyDefaultValueMap.put("driver", "tibcosoftware.jdbc.mongodb.MongoDBDriver");
        propertyDefaultValueMap.put("portNumber", "27017");
        propertyDefaultValueMap.put("timeZone", null);

        // hide the following properties from UI
        Set<String> hiddenPropertySet = getHiddenPropertySet();
        hiddenPropertySet.remove("serverAddress");
        hiddenPropertySet.remove("database");
        hiddenPropertySet.add("driver");
        hiddenPropertySet.add("url");
    }

    @Override
    public Map<String, Object>  customizePropertyValueMap(CustomReportDataSource customReportDataSource, Map<String, Object>  propertyValueMap) {
        if(customReportDataSource.getResources() != null && customReportDataSource.getResources().get(DATA_FILE_RESOURCE_ALIAS) != null){
            // if data source has a data file as a sub resource, then take it's URI as file name property
            propertyValueMap.put(FILE_NAME_PROP, "repo:" + customReportDataSource.getResources()
                    .get(DATA_FILE_RESOURCE_ALIAS).getTargetURI());
        }
        return propertyValueMap;
    }

    /*
    * This function is used for retrieving the metadata layer of the custom data source in form of CustomDomainMetaData
    * CustomDomainMetaData contains information JRFields, query, query language and field name mapping (actual JRField name, name used in domain)
    */
    @Override
    public CustomDomainMetaData getCustomDomainMetaData(CustomReportDataSource customDataSource) throws Exception {
        return null;
    }

}

