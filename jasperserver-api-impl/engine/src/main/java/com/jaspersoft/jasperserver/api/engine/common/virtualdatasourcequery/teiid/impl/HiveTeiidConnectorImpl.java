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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.impl;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid.TeiidDataSource;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.VirtualSQLDataSource;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.TeiidEmbeddedServer;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.TranslatorConfig;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.VirtualReportDataSourceServiceFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.dqp.internal.datamgr.ConnectorManager;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.TranslatorException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: HiveTeiidConnectorImpl.java 50647 2014-10-24 18:22:53Z ichan $
 */
public class HiveTeiidConnectorImpl implements TeiidDataSource, com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.ConnectionFactory {

    String jndiName;
    String dataSourceName;
    String databaseName;
    TranslatorConfig translatorConfig;
    private Map<String, String> importPropertyMap=new HashMap<String, String>();
    private static final Log log = LogFactory.getLog(TeiidEmbeddedServer.class);
    Connection connection;
    String userName = null;
    String password = null;
    String jdbcURL = null;
    String driver = null;
    boolean useDatabaseExplicitly = false;

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public TranslatorConfig getTranslatorConfig() {
        return translatorConfig;
    }

    public void setTranslatorConfig(TranslatorConfig translatorConfig) {
        this.translatorConfig = translatorConfig;
    }

    /*
    * get teiid import properties through spring injection
    */
    public Map<String, String> getImportPropertyMap() {
        return importPropertyMap;
    }

    /*
    * set teiid import properties through spring injection
    */
    public void setImportPropertyMap(Map<String, String> importPropertyMap) {
        this.importPropertyMap = importPropertyMap;
    }

    /*
    * returns connector name which use for reference in teiid connector manager repository (sub data source id)
    */
    public String getConnectorName() {
        return (jndiName).hashCode() + "";
    }

    /*
     * returns connector manager which contains the connection and translator information
     */
    public ConnectorManager getConnectorManager() throws Exception {
        return null;
    }

    public Object getConnectionFactory() throws Exception {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + jndiName);
        if (translatorConfig == null) {
            Connection conn = null;
            try {
                conn = ds.getConnection();
            } catch (SQLException sqlEx) {
                log.debug("Fail to retrieve connection from HIVE data source", sqlEx);
            }
            translatorConfig = new TranslatorConfig();
            if (conn != null && conn.getMetaData().getDatabaseProductName().equalsIgnoreCase("impala")) {
                translatorConfig.setProductName("Impala");
                translatorConfig.setTranslatorName("impala");
                translatorConfig.setTranslatorFactoryClass("org.teiid.translator.hive.ImpalaExecutionFactory");
                // release connection
                conn.close();
            } else {
                translatorConfig.setProductName("hive");
                translatorConfig.setTranslatorName("hive");
                translatorConfig.setTranslatorFactoryClass("org.teiid.translator.hive.HiveExecutionFactory");
            }
                        
        }
        translatorConfig.setupTranslator();
        return ds;
    }

    public String getTranslatorName() {
        return translatorConfig.getTranslatorName();
    }

    public String getSchemaText(Map map) throws Exception {
        return null;
    }

    public String getSchemaSourceType() throws Exception {
        return "native";
    }

    public boolean isUseDatabaseExplicitly() {
        return useDatabaseExplicitly;
    }

    public void setUseDatabaseExplicitly(boolean useDatabaseExplicitly) {
        this.useDatabaseExplicitly = useDatabaseExplicitly;
    }

    public Object getConnectionFactory(Map map) throws Exception {
        userName = (String)map.get("username");
        password = (String)map.get("password");
        jdbcURL = (String)map.get("jdbcURL");
        // workaround for HIVE 0.11 driver
        // it ignore the database name in the URL, and it is fixed in HIVE 0.13 driver
        // we should turn off/ remove this workaround when we upgrade to HIVE 0.13 driver
        //https://issues.apache.org/jira/browse/HIVE-4256
        if (useDatabaseExplicitly) databaseName = getDatabase(jdbcURL);
        driver = null;
        if (jdbcURL.toLowerCase().startsWith("jdbc:hive2://")) driver = "org.apache.hive.jdbc.HiveDriver";
        else if (jdbcURL.toLowerCase().startsWith("jdbc:hive://")) driver = "org.apache.hadoop.hive.jdbc.HiveDriver";
        return new VirtualSQLDataSource(this);

    }

    public Connection createConnection() throws SQLException {
        try {
            if ((connection == null) || connection.isClosed() ) {
                // HIVE DRIVER DOESN'T SUPPORT XAConnection, PooledDataSource
                // JNDI connection is still recommended by HIVE Connection
                Class.forName(driver);
                connection = DriverManager.getConnection(jdbcURL, userName, password);
                if ((databaseName != null)  && !databaseName.toLowerCase().equals("default")) {
                    Statement statement = connection.createStatement();
                    log.debug("HIVE USE NON-DEFAULT DATABASE:  EXECUTE UPDATE - [USE " + databaseName + "]");
                    statement.executeUpdate("USE " + databaseName);
                    statement.close();
                }
            }
            return connection;
        } catch (SQLException sqlEx) { throw sqlEx;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    public TranslatorConfig getTranslator() throws Exception {
        if (translatorConfig == null) {
            Connection conn = null;
            try {
                if ((driver != null) && (jdbcURL != null)) conn = createConnection();
            } catch (SQLException sqlEx) {
                log.debug("Fail to retrieve connection from HIVE data source", sqlEx);
            }
            translatorConfig = new TranslatorConfig();
            if (conn != null && conn.getMetaData().getDatabaseProductName().equalsIgnoreCase("impala")) {
                translatorConfig.setProductName("Impala");
                translatorConfig.setTranslatorName("impala");
                translatorConfig.setTranslatorFactoryClass("org.teiid.translator.hive.ImpalaExecutionFactory");
            } else {
                translatorConfig.setProductName("hive");
                translatorConfig.setTranslatorName("hive");
                translatorConfig.setTranslatorFactoryClass("org.teiid.translator.hive.HiveExecutionFactory");
            }
        }
        translatorConfig.setupTranslator();
        return translatorConfig;
    }

    public ExecutionFactory getTranslatorFactory() throws TranslatorException {
        return translatorConfig.getTranslatorFactory();
    }

    /*
     * returns list of modelMetaData (schema) which is going to be available in virtual data source
     */
    public List<ModelMetaData> getModelMetaDataList() {
        ArrayList<ModelMetaData> modelMetaDataList = new ArrayList<ModelMetaData>();
        modelMetaDataList.add(getModel());
        return modelMetaDataList;
    }

    private ModelMetaData getModel() {
        String subDataSourceID = getConnectorName();
        String modelName = dataSourceName;
        ModelMetaData model = new ModelMetaData();
        model.setModelType(Model.Type.PHYSICAL);
        model.setName(modelName);
    	Properties importProperties = new Properties(){
			private static final long serialVersionUID = 1L;
			{
				setProperty("importer.trimColumnNames", Boolean.TRUE.toString());
			}
		};
        if (importPropertyMap != null) {
        	importProperties.putAll(importPropertyMap);
            }
            model.setProperties(importProperties);
        
        model.addSourceMapping(subDataSourceID, translatorConfig.getTranslatorName(), subDataSourceID);
        return model;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    /***
     * retrieve the database name from JDBC URL
     * for example:
     *      jdbc:hive2://qa-hadoop-master.eng.jaspersoft.com:21050/;auth=noSasl   return default
     *      jdbc:hive2://qa-hadoop-master.eng.jaspersoft.com:10001/default        return default
     *      jdbc:hive2://qa-hadoop-master.eng.jaspersoft.com:21050/non_default;auth=noSasl        return non_default
     * @param jdbcURL
     * @return
     */
    private static String getDatabase(String jdbcURL) {
        int indexForSemiColon  = jdbcURL.indexOf(";");
        String databaseName;
        if (indexForSemiColon > 0) databaseName = jdbcURL.substring(0, indexForSemiColon);
        else databaseName = jdbcURL;
        int lastSeparator = databaseName.lastIndexOf("/");
        if (lastSeparator > 0) databaseName = databaseName.substring(lastSeparator + 1);
        if (databaseName.equals("") || databaseName.contains(":")) databaseName = "default";
        log.debug("HIVE:  DETECT DATABASE NAME  = " + databaseName);
        return databaseName;
    }

}
