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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.VirtualDataSourceException;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.VirtualSQLDataSource;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.VirtualSQLDataSourceMetaData;
import org.teiid.adminapi.impl.ModelMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class TeiidConnectionFactoryImpl implements VirtualSQLDataSourceMetaData {

    private TeiidVirtualDataSourceQueryServiceImpl teiidVirtualDataSourceQueryService;
    private String virtualDSID;
    private Set<String> schemas;
    private Set<String> catalogs;
    private String databaseProductName;
    private String identifierQuoteString;
    private List<ModelMetaData> modelMetaDataList;
    private Map<String, DataSource> subDataSourcesMap;

    public TeiidConnectionFactoryImpl(TeiidVirtualDataSourceQueryServiceImpl teiidVirtualDataSourceQueryService, String virtualDSID,
            List<ModelMetaData> modelMetaDataList, Map<String, DataSource> subDataSourcesMap) {
        this.teiidVirtualDataSourceQueryService = teiidVirtualDataSourceQueryService;
        this.subDataSourcesMap = subDataSourcesMap;
        this.virtualDSID = virtualDSID;
        this.modelMetaDataList = modelMetaDataList;
        // construct schema list form modelMetaDataList
        if (modelMetaDataList != null) {
            schemas = new LinkedHashSet<String>();
            for (ModelMetaData modelMetaData : modelMetaDataList) schemas.add(modelMetaData.getName());
        }
        catalogs = new LinkedHashSet<String>();
        catalogs.add(virtualDSID);
        // define database product name and identifierQuoteString for Teiid
        databaseProductName = "Teiid Embedded";
        identifierQuoteString = "\"";
    }

    // return connection
    public Connection createConnection() throws SQLException {
        VirtualDataSourceException vex = null;
        try {
            // if virtual data source doesn't exist in Teiid embedded server, deploy it in runtime
            if ((!teiidVirtualDataSourceQueryService.isVirtualDataSourceExisted(virtualDSID)) && (modelMetaDataList != null)) {
                teiidVirtualDataSourceQueryService.deployVirtualDataSource(virtualDSID, modelMetaDataList);
            }
            // mark virtual data source is in used
            teiidVirtualDataSourceQueryService.markDataSourceUsed(virtualDSID, AbstractVirtualDataSourceQueryServiceImpl.DataSourceType.MASTER_DATASOURCE, System.currentTimeMillis());
        } catch (Exception ex) {
            teiidVirtualDataSourceQueryService.debug("Teiid: deploy VDB - ", ex);
            vex = getVirtualDataSourceException(ex);
            throw vex;
        }
        try {
            // look up connection from teiid embedded server
            return teiidVirtualDataSourceQueryService.createConnection(virtualDSID);
        } catch (Exception ex) {
            teiidVirtualDataSourceQueryService.debug("Teiid:  failed to connect to VDB.", ex);
            if (vex == null) vex = getVirtualDataSourceException(ex);
            try {
                teiidVirtualDataSourceQueryService.undeployVirtualDataSource(virtualDSID);
            } catch (Exception ex2) {};
            throw vex;
        }
    }

    private VirtualDataSourceException getVirtualDataSourceException(Throwable ex) {
        if (ex instanceof VirtualDataSourceException) return (VirtualDataSourceException)ex;
        VirtualDataSourceException virtualDataSourceException = new VirtualDataSourceException(ex);
            virtualDataSourceException.setVirtualDataSourceID(virtualDSID);
            virtualDataSourceException.setSchemas(schemas);
            return  virtualDataSourceException;
    }

    // return schemas for this virtual data source
    public Set<String> getSchemas() throws SQLException {
        return schemas;
    }

    // return catalogs for this virtual data source
    public Set<String> getCatalogs() throws SQLException {
        return catalogs;
    }

    // return database product name for this virtual data source
    public String getDatabaseProductName() throws SQLException {
        return databaseProductName;
    }

    // return identifier quote string for this virtual data source
    public String getIdentifierQuoteString() throws SQLException {
        return identifierQuoteString;
    }

    // return sub-datasource connection
    private Connection getSubDataSourceConnection(String subDataSourceName) throws SQLException {
        return teiidVirtualDataSourceQueryService.getDataSource(subDataSourcesMap.get(subDataSourceName)).getConnection();
    }

    private Map<String, String> getPossibleDataSourceSchemaCombo(String vdsSchemaPattern) throws Exception {
        Map<String, String> dataSourceSchemaCombo = new HashMap<String, String>();
        for (String subDataSourceID : subDataSourcesMap.keySet()) {
            if (vdsSchemaPattern.equalsIgnoreCase(subDataSourceID)) {
                dataSourceSchemaCombo.put(subDataSourceID, null);
            } else if (vdsSchemaPattern.startsWith(subDataSourceID + "_")) {
                dataSourceSchemaCombo.put(subDataSourceID, vdsSchemaPattern.substring(subDataSourceID.length() + 1));
            }
        }
        return dataSourceSchemaCombo;
    }


    /*
    *  Teiid creates temp tables in the VDS and we should not display those temp tables in domain designer
    *  This method will go to each sub-data source and retrieve the original table list
    */
    public Set<String> getSubDSTableList(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        if (teiidVirtualDataSourceQueryService.isUseSubDSTableList()) {
            Connection conn = null;
            ResultSet rs = null;
            try {
                Map<String, String> dataSourceSchemaMap = getPossibleDataSourceSchemaCombo(schemaPattern);

                for (Map.Entry<String, String> entry: dataSourceSchemaMap.entrySet()) {
                    conn= getSubDataSourceConnection(entry.getKey());
                    if (entry.getValue() != null) {
                        Set<String> schemaList = VirtualSQLDataSource.discoverSchemas(conn);
                        if (!schemaList.contains(entry.getValue())) continue;
                    }
                    rs = conn.getMetaData().getTables(catalog, entry.getValue(), tableNamePattern, types);
                    Set<String> result = new HashSet();
                    while (rs.next()) {
                        result.add(rs.getString("TABLE_NAME"));
                    }
                    try { rs.close();
                    } catch (Exception ex) {};

                    return result;
                }
            } catch (Exception ex) {
                teiidVirtualDataSourceQueryService.debug("Unable to retrieve original table list.  Use the table list from VDS instead.");
            } finally {
                try { if (rs != null) rs.close(); } catch (Exception ex) {};
                try { if (conn != null) conn.close(); } catch (Exception ex) {};
            }
        }
        return null;
    }

}
