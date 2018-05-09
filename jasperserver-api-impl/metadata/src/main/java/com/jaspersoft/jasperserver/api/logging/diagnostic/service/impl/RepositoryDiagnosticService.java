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
package com.jaspersoft.jasperserver.api.logging.diagnostic.service.impl;

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ogavavka
 */
public class RepositoryDiagnosticService implements Diagnostic {
    private DataSource dataSource;
    private Map<String,String> metaDataMap;
    @Override
    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
        if (metaDataMap==null)
            fillMetaDataMap();
        return new DiagnosticAttributeBuilder()
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_PRODUCT_NAME, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_PRODUCT_NAME);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_PRODUCT_VERSION, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_PRODUCT_VERSION);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DRIVER_NAME, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DRIVER_NAME);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.SQL_KEYWORDS, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.SQL_KEYWORDS);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_URL, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_URL);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_USER_NAME, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_USER_NAME);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.JDBC_MAJOR_VERSION, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.JDBC_MAJOR_VERSION);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.JDBC_MINOR_VERSION, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.JDBC_MINOR_VERSION);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_MAX_ROW_SIZE, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_MAX_ROW_SIZE);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_MAX_STATEMENT_LENGTH, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_MAX_STATEMENT_LENGTH);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_MAX_CONNECTIONS, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_MAX_CONNECTIONS);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_MAX_CHAR_LENGTH, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_MAX_CHAR_LENGTH);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMNS_TABLE, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMNS_TABLE);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMNS_SELECT, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMNS_SELECT);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMNS_GROUP, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMNS_GROUP);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMN_NAME_LENGTH, new DiagnosticCallback<String>() {
                    @Override
                    public String getDiagnosticAttributeValue() {
                        return metaDataMap.get(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMN_NAME_LENGTH);
                    }
                })
                .build();
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void fillMetaDataMap(){
        metaDataMap = new HashMap<String, String>();
        try {
            DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_PRODUCT_NAME, metaData.getDatabaseProductName());
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_PRODUCT_VERSION, metaData.getDatabaseProductVersion());
            metaDataMap.put(DiagnosticAttributeBuilder.DRIVER_NAME, metaData.getDriverName());
            metaDataMap.put(DiagnosticAttributeBuilder.SQL_KEYWORDS, metaData.getSQLKeywords());
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_URL, metaData.getURL());
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_USER_NAME, metaData.getUserName());
            metaDataMap.put(DiagnosticAttributeBuilder.JDBC_MAJOR_VERSION, String.valueOf(metaData.getJDBCMajorVersion()));
            metaDataMap.put(DiagnosticAttributeBuilder.JDBC_MINOR_VERSION, String.valueOf(metaData.getJDBCMinorVersion()));
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_MAX_ROW_SIZE, String.valueOf(metaData.getMaxRowSize()));
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_MAX_STATEMENT_LENGTH, String.valueOf(metaData.getMaxStatementLength()));
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_MAX_CONNECTIONS, String.valueOf(metaData.getMaxConnections()));
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_MAX_CHAR_LENGTH, String.valueOf(metaData.getMaxCharLiteralLength()));
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMNS_TABLE, String.valueOf(metaData.getMaxColumnsInTable()));
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMNS_SELECT, String.valueOf(metaData.getMaxColumnsInSelect()));
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMNS_GROUP, String.valueOf(metaData.getMaxColumnsInGroupBy()));
            metaDataMap.put(DiagnosticAttributeBuilder.DATABASE_MAX_COLUMN_NAME_LENGTH, String.valueOf(metaData.getMaxColumnNameLength()));
            metaData.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
