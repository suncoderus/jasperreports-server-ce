package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.translator.jdbc.mongodb;

import org.teiid.core.types.DataTypeManager;
import org.teiid.metadata.Column;
import org.teiid.metadata.MetadataFactory;
import org.teiid.metadata.Table;
import org.teiid.translator.MetadataProcessor;
import org.teiid.translator.Translator;
import org.teiid.translator.jdbc.JDBCExecutionFactory;
import org.teiid.translator.jdbc.JDBCMetdataProcessor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andriy Godovanets
 * @version $Id$
 */
@Translator(name="mongodb-jdbc", deprecated = "MongoDB JDBC-compatible translator")
public class MongoDBExecutionFactory extends JDBCExecutionFactory {

    public MongoDBExecutionFactory() {
    }

    @Override
    public MetadataProcessor<Connection> getMetadataProcessor() {
        return new JDBCMetdataProcessor() {
            @Override
            protected Column addColumn(ResultSet columns, Table table, MetadataFactory metadataFactory, int rsColumns) throws SQLException {
                Column column = super.addColumn(columns, table, metadataFactory, rsColumns);
                // agodovan@tibco.com
                // see https://jira.tibco.com/browse/JS-33780
                // see https://issues.jboss.org/browse/TEIID-5237 - an issue with matching against mongodb documents with a mixed type (string and integer) column
                // This is workaround, until TEIID will fix theirs bug TEIID-5237 since the integer values are implicitly convertible to string for projection,
                // we've disabled comparison for the MONGO_ID_COLUMN and let the engine handle it
                if (DataTypeManager.DefaultDataTypes.STRING.equalsIgnoreCase(column.getNativeType())) {
                    // to disable comparison, mongodb 'string' column is declared with option (searchable 'unsearchable')
                    column.setSearchType(Column.SearchType.Unsearchable);
                }
                return column;
            }
        };
    }
}
