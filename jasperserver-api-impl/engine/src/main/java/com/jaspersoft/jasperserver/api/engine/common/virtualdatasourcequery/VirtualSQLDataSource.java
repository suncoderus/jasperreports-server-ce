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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.ConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.String;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class VirtualSQLDataSource implements javax.sql.DataSource {

    ConnectionFactory connectionFactory;
    Connection connection = null;
    private static final String TABLE_SCHEM = "TABLE_SCHEM";
    private static final String TABLE_CAT = "TABLE_CAT";
    private static final String TABLE_NAME = "TABLE_NAME";

    private static final Log log = LogFactory.getLog(VirtualSQLDataSource.class);


    public VirtualSQLDataSource(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /*
     * return SQL connection
     */
    public Connection getConnection() throws SQLException {
            if ((connection != null) && !connection.isClosed()) return connection;
            connection = connectionFactory.createConnection();
        //    testConnection();
            return connection;
    }

     /*
     * return SQL connection
     */
    public Connection getConnection(String username, String password) throws SQLException {
            if ((connection != null) && !connection.isClosed()) return connection;
            connection = connectionFactory.createConnection();
         //   testConnection();
            return connection;
    }

    /*
     * return schema list from connection
     */
    public Set<String> getSchemas() throws SQLException {
        Set<String> schemas = null;
        if (connectionFactory instanceof VirtualSQLDataSourceMetaData) schemas = ((VirtualSQLDataSourceMetaData)connectionFactory).getSchemas();
        if (schemas == null) return discoverSchemas(getConnection());
        else return schemas;
    }

    /*
     * return catalog list from connection
     */
    public Set<String> getCatalogs() throws SQLException  {
        Set<String> catalogs = null;
        if (connectionFactory instanceof VirtualSQLDataSourceMetaData) catalogs = ((VirtualSQLDataSourceMetaData)connectionFactory).getCatalogs();
        if (catalogs == null) return discoverCatalogs(getConnection());
        return catalogs;
    }

    /*
    *  Teiid creates temp tables in the VDS and we should not display those temp tables in domain designer
    *  This method will go to each sub-data source and retrieve the original table list
    *  return NULL if not able to retrieve sub data source table list
    */
    public Set<String> getSubDSTableList(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        if (connectionFactory instanceof VirtualSQLDataSourceMetaData)
            return ((VirtualSQLDataSourceMetaData)connectionFactory).getSubDSTableList(catalog, schemaPattern, tableNamePattern, types);
        throw new SQLException("This method only works JDBC/ JNDI sub data source for virtual data source");
    }

    /*
     * return database product name from connection
     */
    public String getDatabaseProductName() throws SQLException {
        String databaseProductName = null;
        if (connectionFactory instanceof VirtualSQLDataSourceMetaData) databaseProductName = ((VirtualSQLDataSourceMetaData)connectionFactory).getDatabaseProductName();
        if (databaseProductName == null) return getConnection().getMetaData().getDatabaseProductName();
        return databaseProductName;
    }

    /*
     * return identifier quote string from connection
     */
    public String getIdentifierQuoteString() throws SQLException {
        String identifierQuoteString = null;
        if (connectionFactory instanceof VirtualSQLDataSourceMetaData) identifierQuoteString = ((VirtualSQLDataSourceMetaData)connectionFactory).getIdentifierQuoteString();
        if (identifierQuoteString == null) return getConnection().getMetaData().getDatabaseProductName();
        return identifierQuoteString;
    }

    public PrintWriter getLogWriter() throws SQLException {
        PrintWriter logWriter = null;
        logWriter = new PrintWriter(new LoggerOutputStream());
        return logWriter;
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(new PrintWriter(new LoggerOutputStream()));
    }

    public void setLoginTimeout(int seconds) throws SQLException {};

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger("com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequeryVirtualSQLDataSource");
    }

    public <T> T unwrap(java.lang.Class<T> iface) throws java.sql.SQLException {
        return null;
    }


    public boolean isWrapperFor(java.lang.Class<?> iface) throws java.sql.SQLException {
        return false;
    }

    // write debug messages into the log
    class LoggerOutputStream extends OutputStream {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        public void write(int b) {
            if (b != '\n') bOut.write(b);
            log.debug(bOut.toString());
        }
    }

    // find schemas from database metadata
    public static Set<String> discoverSchemas(Connection conn) throws SQLException {
        DatabaseMetaData dbMetaData = conn.getMetaData();
        ResultSet rs = null;
        try {
            Set<String> set = new LinkedHashSet<String>();
            rs = dbMetaData.getSchemas();
            while (rs.next()) {
                String schema = rs.getString(TABLE_SCHEM);
                if (schema != null) set.add(schema);
            }
            return set;
        } catch (SQLException ex) {
            log.error("Cannot get schemas", ex);
            throw ex;
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (Exception ex) { }
            }
        }
    }

    /**
     * Retrieves all nonempty schemas from the specified connection.
     * @param conn a connection
     * @param databaseObjectTypesFilter a list of table types, which must be from the list of table types
     *         returned from {@link DatabaseMetaData#getTableTypes}, to include; <code>null</code> returns all types
     * @return
     */
    public static Set<String> discoverNonEmptySchemas(Connection conn, Set<String> databaseObjectTypesFilter) throws SQLException {
        DatabaseMetaData dbMetaData = conn.getMetaData();
        try {
            Set<String> set = new LinkedHashSet<String>();
           Set<String> schemaList = getResult(dbMetaData.getSchemas(), TABLE_SCHEM);
           String[] types = databaseObjectTypesFilter.toArray(new String[databaseObjectTypesFilter.size()]);

            for (String schema : schemaList) {
                ResultSet rs2 = null;
                try {

                    rs2 = conn.getMetaData().getTables(null, schema, null, types);
                    if (rs2.next()) set.add(schema);
                    else log.debug(schema + " schema contains no table.  Ignore in VDS");

                    /***  take too long to check empty tables
                    Set<String> tableList = getResult(conn.getMetaData().getTables(null, schema, null, types), TABLE_NAME);
                    Set<String> tableWithColsList = getResult(conn.getMetaData().getColumns(null, schema, null, null), TABLE_NAME);

                    if (tableList.size() == 0) {
                        log.debug(schema + " schema contains no table.  Ignore in VDS");
                        continue;
                    } else {
                        set.add(schema);

                        // does all tables contain columns
                        boolean doesAllTablesContainCols = true;
                        for (String tableName : tableList) {
                            if (!tableWithColsList.contains(tableName)) {
                                log.debug(schema + "." +  tableName + " table contains table with no column.  Ignore in VDS");
                                doesAllTablesContainCols = false;
                                break;
                            }
                        }
                        if (doesAllTablesContainCols) set.add(schema);

                    }
                    ****/
                } catch  (SQLException ex2) {
                    log.debug("Fail to read schema, " + schema + ".  Ignore in VDS");
                    ex2.printStackTrace();

                } finally {
                    if (rs2 != null) rs2.close();
                }
            }
            return set;
        } catch (SQLException ex) {
            log.error("Cannot get schemas", ex);
            throw ex;
        }
    }

    private static Set<String> getResult(ResultSet rs, String columnName) {
        Set<String> result = new HashSet<String>();
        try {
        while (rs.next()) {
            result.add(rs.getString(columnName));
        }
        } catch (Exception ex) {};
        try { rs.close();
        } catch (Exception ex) {};
        return result;
    }

    // find catalogs from database metadata
    public static Set discoverCatalogs(Connection conn) throws SQLException {
        DatabaseMetaData dbMetaData = conn.getMetaData();
        ResultSet rs = null;
        try {
            Set set = new LinkedHashSet();
            rs = dbMetaData.getCatalogs();
            while (rs.next()) {
                String catalog = rs.getString(TABLE_CAT);
                set.add(catalog);
            }
            return set;
        } catch (SQLException ex) {
            log.error("Cannot get catalogs", ex);
            throw ex;
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (Exception ex) { }
            }
        }
    }

    /*
     * test connection
     */
    private void testConnection() throws SQLException {
        DatabaseMetaData dbMetaData = connection.getMetaData();
        System.out.println("MetaDataConstants.DATASOURCE_VENDOR = [" + dbMetaData.getDatabaseProductName() + "]");
        System.out.println("MetaDataConstants.NAME_QUOTE_CHAR = [" + dbMetaData.getIdentifierQuoteString() + "]");
        Set set = discoverSchemas(connection);
        for (Object val : set) System.out.println("SCHEMA = " + val);
    }
}
