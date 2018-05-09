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
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.TeiidEmbeddedServer;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.TranslatorConfig;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.olap4j.OlapConnection;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.dqp.internal.datamgr.ConnectorManager;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.TranslatorException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class OlapTeiidDataSourceImpl implements TeiidDataSource {

    String repoURI;
    OlapConnectionService olapConnectionService;
    TranslatorConfig translatorConfig;
    String dataSourceName;
    private Map<String, String> importPropertyMap;
    private static final Log log = LogFactory.getLog(TeiidEmbeddedServer.class);

    public String getRepoURI() {
        return repoURI;
    }

    public void setRepoURI(String repoURI) {
        this.repoURI = repoURI;
    }

    public OlapConnectionService getOlapConnectionService() {
		return olapConnectionService;
	}

	public void setOlapConnectionService(OlapConnectionService olapConnectionService) {
		this.olapConnectionService = olapConnectionService;
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
        return (repoURI).hashCode() + "";
    }

    /*
     * returns connector manager which contains the connection and translator information
     */
    public ConnectorManager getConnectorManager() throws Exception {
        return null;
    }

    public Object getConnectionFactory() throws Exception {
        OlapConnection olapConnection = olapConnectionService.getOlapConnection(null, repoURI);
        DataSource dataSource = new SimpleOLAPDataSource(olapConnection);
        translatorConfig.setupTranslator();
        return dataSource;
    }

    public String getTranslatorName() {
        return translatorConfig.getTranslatorName();
    }

    public Object getConnectionFactory(Map map) throws Exception {
        throw new Exception("Not Supported");
    }

    public TranslatorConfig getTranslator() throws Exception {
        throw new Exception("Not Supported");
    }

    public String getSchemaText(Map map) throws Exception {
        throw new Exception("Not Supported");
    }

    public String getSchemaSourceType() throws Exception {
        throw new Exception("Not Supported");
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
        if (importPropertyMap != null) {
            Properties importProperties = new Properties();
            for (Map.Entry<String, String> entry : importPropertyMap.entrySet()) {
                importProperties.setProperty(entry.getKey(), entry.getValue());
            }
            model.setProperties(importProperties);
        }
        model.addSourceMapping(subDataSourceID, translatorConfig.getTranslatorName(), subDataSourceID);
        return model;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }


public class SimpleOLAPDataSource implements DataSource {
    private Connection connection;
	private PrintWriter pw = new PrintWriter(System.out);
	private int loginTimeout = 0;

	public SimpleOLAPDataSource(Connection connection) {
		this.connection = connection;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return pw;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		pw = out;
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		loginTimeout = seconds;
	}

	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public Connection getConnection() throws SQLException {
		return connection;
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		return connection;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}
}



}
