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
package com.jaspersoft.jasperserver.remote.connection.datadiscovery;

import com.jaspersoft.jasperserver.dto.connection.datadiscovery.FlatDataSet;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceSingleElement;
import com.jaspersoft.jasperserver.dto.resources.domain.SchemaElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class JdbcQueryExecutor implements QueryExecutor<String, Connection, FlatDataSet, ResourceGroupElement> {
    protected final Log log = LogFactory.getLog(getClass());
    protected SQLQueryValidator validator;

    public JdbcQueryExecutor(SQLQueryValidator validator) {
        this.validator = validator;
    }

    @Override
    public FlatDataSet executeQuery(String query, Connection connection) {
        return executeQuery(query, connection, false);
    }

    @Override
    public ResourceGroupElement executeQueryForMetadata(String query, Connection connection) {
        return executeQuery(query, connection, true).getMetadata();
    }

    protected FlatDataSet executeQuery(String query, Connection connection, boolean skipData) {
        validator.validate(query);
        final List<String[]> result = new ArrayList<String[]>();
        final Map<String, Class<?>> columns;
        final ArrayList<SchemaElement> columnElements = new ArrayList<SchemaElement>();
        final ResourceGroupElement metadata = new ResourceGroupElement().setKind("resultSet").setElements(columnElements);
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            columns = new HashMap<String, Class<?>>();
            final ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                final String columnClassName = metaData.getColumnClassName(i);
                final String columnName = metaData.getColumnName(i);
                if (columns.containsKey(columnName)) {
                    throw new QueryExecutionException("The query returned more than one field with the same name: "
                            + columnName
                            + " To use the fields, rename them with unique aliases. For example, you could " +
                            "rename a.account_id as my_account_a_id. ", query, null);
                }
                columnElements.add(new ResourceSingleElement().setName(columnName).setType(columnClassName));
                try {
                    columns.put(columnName, Class.forName(columnClassName));
                } catch (ClassNotFoundException e) {
                    // shouldn't happen, but let's fail gracefully if happen.
                    throw new QueryExecutionException("Column metadata processing failed. Column class not found. " +
                            "Column name: " + columnName + " Column class: " + columnClassName, query, e);
                }
            }
            if(!skipData) {
                while (resultSet.next()) {
                    final int size = columnElements.size();
                    String[] row = new String[size];
                    for (int i = 0; i < size; i++) {
                        SchemaElement column = columnElements.get(i);
                        row[i] = resultSet.getString(column.getName());
                    }
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            throw new QueryExecutionException(query, e);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new QueryExecutionException("Query parsing exception", query, null);
        } finally {
            if(stmt != null){
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error(e);
                }
            }
        }
        return new FlatDataSet().setData(result).setMetadata(metadata);
    }
}
