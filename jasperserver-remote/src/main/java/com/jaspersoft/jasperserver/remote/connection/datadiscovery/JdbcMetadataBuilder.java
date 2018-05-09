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

import com.jaspersoft.jasperserver.api.ExceptionListWrapper;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.dto.connection.datadiscovery.DotByTildaEscapeUtil;
import com.jaspersoft.jasperserver.dto.connection.datadiscovery.UnexpectedEscapeCharacterException;
import com.jaspersoft.jasperserver.dto.connection.metadata.PartialMetadataOptions;
import com.jaspersoft.jasperserver.dto.resources.domain.AbstractResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceMetadataSingleElement;
import com.jaspersoft.jasperserver.dto.resources.domain.SchemaElement;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class JdbcMetadataBuilder implements MetadataBuilder<Connection>,
        ParametrizedMetadataBuilder<Connection, PartialMetadataOptions> {
    private SpecialCharacterEscape characterEscape;
    private DotByTildaEscapeUtil dotByTildaEscapeUtil = new DotByTildaEscapeUtil();

    public JdbcMetadataBuilder(){
    }

    public JdbcMetadataBuilder(SpecialCharacterEscape characterEscape){
        this.characterEscape = characterEscape;
    }

    private static final Map<Integer, String> JDBC_TYPES_BY_CODE = Collections.unmodifiableMap(new HashMap<Integer, String>() {{
        put(Types.BIGINT, LONG);
        put(Types.BIT, BOOLEAN);
        put(Types.BOOLEAN, BOOLEAN);
        put(Types.CHAR, STRING);
        put(Types.DATE, DATE);
        put(Types.DECIMAL, BIG_DECIMAL);
        put(Types.DOUBLE, DOUBLE);
        put(101, BIG_DECIMAL);
        put(Types.FLOAT, DOUBLE);
        put(100, BIG_DECIMAL);
        put(Types.INTEGER, INTEGER);
        put(Types.LONGVARCHAR, STRING);
        put(Types.NUMERIC, BIG_DECIMAL);
        put(Types.REAL, FLOAT);
        put(Types.SMALLINT, SHORT);
        put(Types.TIME, TIME);
        put(Types.TIMESTAMP, TIMESTAMP);
        put(-101, TIMESTAMP);
        put(-102, TIMESTAMP);
        put(Types.TINYINT, BYTE);
        put(Types.VARCHAR, STRING);
        put(Types.NVARCHAR, STRING);
    }});


    @Override
    public SchemaElement build(Connection connection, PartialMetadataOptions options) {
        final List<String> includes = options.getIncludes();
        final List<String> expands = options.getExpands();
        List<SchemaElement> items;
        try {
            final DatabaseMetaData metaData = connection.getMetaData();

            if (includes != null && includes.size() > 0) {
                items = includeMetadata(includes, metaData);
            } else {
                final Map<String, List<String[]>> expandsMap = new HashMap<String, List<String[]>>();
                if (expands != null) {
                    List<UnexpectedEscapeCharacterException> escapeExceptions = new ArrayList<UnexpectedEscapeCharacterException>();
                    for (String expand : expands) {
                        String[] tokens = null;
                        try {
                            tokens = dotByTildaEscapeUtil.splitByDotUnEscapeTokens(expand);
                        } catch (UnexpectedEscapeCharacterException e){
                            escapeExceptions.add(e);
                        }
                        if(tokens != null) {
                            List<String[]> tokensList = expandsMap.get(tokens[0]);
                            if (tokensList == null) {
                                tokensList = new ArrayList<String[]>();
                                expandsMap.put(tokens[0], tokensList);
                            }
                            if (tokens.length > 1) {
                                tokensList.add(Arrays.copyOfRange(tokens, 1, tokens.length));
                            }
                        }
                    }
                    handleEscapeExceptions(escapeExceptions, "expand");
                }
                items = expandMetadata(expandsMap, metaData);
            }
        } catch (SQLException e) {
            throw new JSExceptionWrapper(e);
        }
        if (items == null) {
            return null;
        }
        return new ResourceGroupElement().setElements(items);
    }

    protected void handleEscapeExceptions(List<UnexpectedEscapeCharacterException> escapeExceptions, String parameterName){
        if(!escapeExceptions.isEmpty()){
            List<IllegalParameterValueException> exceptionList =
                    new ArrayList<IllegalParameterValueException>(escapeExceptions.size());
            for (UnexpectedEscapeCharacterException escapeException : escapeExceptions) {
                exceptionList.add(new IllegalParameterValueException(parameterName, escapeException.getString(), escapeException));
            }
            throw new ExceptionListWrapper(exceptionList);
        }
    }

    public SchemaElement build(Connection connection, Map<String, String[]> options) {
        final List<String> expands = options != null && options.get("expand") != null ? Arrays.asList(options.get("expand")) : null;
        final List<String> includes = options != null && options.get("include") != null ? Arrays.asList(options.get("include")) : null;
        return build(connection, new PartialMetadataOptions().setExpands(expands).setIncludes(includes));
    }

    protected List<SchemaElement> expandMetadata(Map<String, List<String[]>> expandsMap, DatabaseMetaData metaData) throws SQLException {
        List<SchemaElement> result;
        final String databaseProductName = characterEscape != null ? metaData.getDatabaseProductName() : null;
        final List<String> schemas = new ArrayList<String>();
        final ResultSet resultSet = metaData.getSchemas();
        if(resultSet.isBeforeFirst()){
            result = new ArrayList<SchemaElement>();
            while (resultSet.next()) {
                schemas.add(resultSet.getString("TABLE_SCHEM"));
            }
            for (String schema : schemas) {
                result.add(getSchemaMetadata(schema, expandsMap.get(schema), metaData, databaseProductName));
            }
        } else {
            // this data source doesn't have any schema. Let's load tables then
            // why expandsMap.keySet() - in this case tables are keys.
            result = buildTablesMetadata(null, metaData, expandsMap.keySet(), databaseProductName);
        }
        return result;

    }

    protected List<SchemaElement> includeMetadata(List<String> includes, DatabaseMetaData metaData) throws SQLException {
        final boolean hasSchemas = metaData.getSchemas().isBeforeFirst();
        final String databaseProductName = characterEscape != null ? metaData.getDatabaseProductName() : null;
        List<SchemaElement> result = new ArrayList<SchemaElement>();
        List<UnexpectedEscapeCharacterException> escapeExceptions = new ArrayList<UnexpectedEscapeCharacterException>();
        for (String include : includes) {
            SchemaElement currentItem = null;
            if (include != null) {
                String[] path = null;
                try {
                    path = dotByTildaEscapeUtil.splitByDotUnEscapeTokens(include);
                } catch (UnexpectedEscapeCharacterException e){
                    escapeExceptions.add(e);
                }
                if(path != null && escapeExceptions.isEmpty()) {
                    switch (path.length) {
                        case 1:
                            if (hasSchemas) {
                                currentItem = getSchemaMetadata(path[0], new ArrayList<String[]>(), metaData, databaseProductName);
                            } else {
                                currentItem = getTableMetadata(null, path[0], true, metaData, databaseProductName);
                            }
                            break;
                        case 2:
                            if (hasSchemas) {
                                currentItem = getTableMetadata(path[0], path[1], true, metaData, databaseProductName);
                            } else {
                                currentItem = getColumnMetadata(null, path[0], path[1], true, metaData, databaseProductName);
                            }
                            break;
                        case 3:
                            currentItem = getColumnMetadata(path[0], path[1], path[2], true, metaData, databaseProductName);
                            break;
                    }
                }
            }
            if (currentItem != null) {
                result.add(currentItem);
            }
        }
        handleEscapeExceptions(escapeExceptions, "include");
        return result;
    }

    protected SchemaElement getSchemaMetadata(String schema, List<String[]> expand, DatabaseMetaData metaData,
            String databaseProductName) throws SQLException {
        ResourceGroupElement result = null;
        final ResultSet resultSet = metaData.getSchemas(null,
                characterEscape != null ? characterEscape.escape(schema, databaseProductName) : schema);
        if(resultSet.isBeforeFirst()) {
            result = new ResourceGroupElement().setKind("schema").setName(schema);
            if (expand != null) {
                final List<SchemaElement> tableItems;
                final Set<String> tableNamesToExpand = new HashSet<String>();
                for (String[] strings : expand) {
                    tableNamesToExpand.add(strings[0]);
                }
                tableItems = buildTablesMetadata(schema, metaData, tableNamesToExpand, databaseProductName);
                result.setElements(tableItems);
            }
        }
        return result;
    }

    protected List<SchemaElement> buildTablesMetadata(String schema, DatabaseMetaData metaData,
            Set<String> tableNamesToExpand, String databaseProductName) {
        List<SchemaElement> tableItems = new ArrayList<SchemaElement>();
        try {
            final String escapedSchema = schema != null && characterEscape != null ? characterEscape.escape(schema, databaseProductName) : schema;
            final ResultSet tables = metaData.getTables(null,
                    escapedSchema,
                    null, new String[]{"TABLE", "VIEW", "ALIAS", "SYNONYM"});
            while (tables.next()) {
                final String tableName = tables.getString("TABLE_NAME");
                tableItems.add(getTableMetadata(schema, tableName, tableNamesToExpand.contains(tableName), metaData,
                        databaseProductName));
            }
        } catch (SQLException e) {
            throw new JSExceptionWrapper(e);
        }
        return tableItems;
    }

    protected SchemaElement getTableMetadata(String schema, String table, boolean expand,
            DatabaseMetaData metaData, String databaseProductName) throws SQLException {
        AbstractResourceGroupElement result = null;
        final String escapedSchema = schema != null && characterEscape != null ?
                characterEscape.escape(schema, databaseProductName) : schema;
        final String escapedTableName = table != null && characterEscape != null ?
                characterEscape.escape(table, databaseProductName) : table;
        final ResultSet tables = metaData.getTables(null, escapedSchema, escapedTableName, new String[]{"TABLE", "VIEW", "ALIAS", "SYNONYM"});
        if(tables.isBeforeFirst()) {
            result = new ResourceGroupElement().setName(table).setKind("table");
            if (expand) {
                List<SchemaElement> columnsMetadata = fillColumnsMetadata(schema, table, metaData, databaseProductName);
                if (!columnsMetadata.isEmpty()) {
                    result.setElements(columnsMetadata);
                }
            }
        }
        return result;
    }

    protected SchemaElement getColumnMetadata(String schema, String table, String column, boolean expand,
            DatabaseMetaData metaData, String databaseProductName) {
        SchemaElement columnItem = null;
        if (expand) {
            try {
                List<SchemaElement> columnsMetadata = fillColumnsMetadata(schema, table, metaData, databaseProductName);
                for (SchemaElement element : columnsMetadata) {
                    if (element.getName().equals(column)) {
                        columnItem = element;
                        break;
                    }
                }
            } catch (SQLException e) {
                throw new JSExceptionWrapper(e);
            }
        }
        return columnItem;
    }

    private List<SchemaElement> fillColumnsMetadata(String schema, String table, DatabaseMetaData metaData,
            String databaseProductName) throws SQLException {
        List<SchemaElement> columnsMetadata = new ArrayList<SchemaElement>();
        final String escapedSchema = characterEscape != null ? characterEscape.escape(schema, databaseProductName) : schema;
        final String escapedTableName = characterEscape != null ? characterEscape.escape(table, databaseProductName) : table;
        final ResultSet columns = metaData.getColumns(null, escapedSchema, escapedTableName, null);
        if(columns.isBeforeFirst()) {
            ResultSet primaryKeySet = metaData.getPrimaryKeys(null, escapedSchema, escapedTableName);
            List<String> primaryKeys = new ArrayList<String>();
            while (primaryKeySet.next()) {
                primaryKeys.add(primaryKeySet.getString(4));
            }
            final ResultSet foreignKeysSet = metaData.getImportedKeys(null, escapedSchema, escapedTableName);
            Map<String, String> foreignKeyMap = new HashMap<String, String>();
            while (foreignKeysSet.next()) {
                String foreignKeyColumnName = foreignKeysSet.getString("FKCOLUMN_NAME");
                String primaryKeyTableName = foreignKeysSet.getString("PKTABLE_NAME");
                String primaryKeyColumnName = foreignKeysSet.getString("PKCOLUMN_NAME");
                String primaryKeySchemaName = foreignKeysSet.getString("PKTABLE_SCHEM");
                foreignKeyMap.put(foreignKeyColumnName,
                        (primaryKeySchemaName != null ? primaryKeySchemaName + "." : "")
                                + primaryKeyTableName + "." + primaryKeyColumnName);
            }
            while (columns.next()) {
                final String columnName = columns.getString("COLUMN_NAME");
                int typeCode = columns.getInt("DATA_TYPE");
                final ResourceMetadataSingleElement columnItem = new ResourceMetadataSingleElement().setName(columnName).setType(JDBC_TYPES_BY_CODE.get(typeCode));
                if (primaryKeys.contains(columnName)) {
                    columnItem.setIsIdentifier(true);
                }
                if (foreignKeyMap.containsKey(columnName)) {
                    columnItem.setReferenceTo(foreignKeyMap.get(columnName));
                }
                columnsMetadata.add(columnItem);
            }
        }
        return columnsMetadata;
    }
}
