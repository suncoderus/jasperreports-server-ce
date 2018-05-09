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

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.olap.Olap4jDataSource;

import org.olap4j.Axis;
import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapStatement;
import org.olap4j.Position;
import org.olap4j.metadata.Member;

/**
 * A helper class. Queries an XMLA server and creates field list and a datasource.
 * Uses {@link org.olap4j.driver.xmla.XmlaOlap4jDriver} by default.
 */
public class XmlaQueryDataSourceHelper {

    private Map<String, Object> properties;
    private List<JRField> fields = new ArrayList<JRField>();
    private CellSet cellSet;
    private String dirverClassName = "org.olap4j.driver.xmla.XmlaOlap4jDriver";

    /**
     * Creates a new instance using following properties:
     * <pre>
     * xmlaUrl
     * username
     * password
     * query
     * </pre>
     * @param properties - datasource properties
     */
    public XmlaQueryDataSourceHelper(Map<String, Object> properties) {
        this.properties = properties;
        createFields();
    }

    private void createFields() {
        String xmlaUrl = (String) properties.get("xmlaUrl");
        String username = (String) properties.get("username");
        String password = (String) properties.get("password");
        String query = (String) properties.get("query");

        HashMap<String, String> fieldName2DescriptionMap = getFieldDescriptions(xmlaUrl, username, password, query);

        for (Entry<String, String> nameAndDescriptoin : fieldName2DescriptionMap.entrySet()) {
            fields.add(createField(nameAndDescriptoin.getKey(), nameAndDescriptoin.getValue(), String.class.getName()));
        }

    }

    private HashMap<String, String> getFieldDescriptions(String xmlaUrl, String username, String password, String query) {
        HashMap<String, String> fieldDescriptions = new HashMap<String, String>();

        try {
            Class.forName(dirverClassName);
            Connection connection = DriverManager.getConnection("jdbc:xmla:Server=" + xmlaUrl, username, password);
            try {
                OlapConnection olapConnection = connection.unwrap(OlapConnection.class);
                OlapStatement statement = olapConnection.createStatement();
                try {
                    cellSet = statement.executeOlapQuery(query);

                    for (Position row : cellSet.getAxes().get(Axis.ROWS.axisOrdinal())) {
                        for (Member member : row.getMembers()) {
                            String levelName = member.getLevel().getUniqueName();
                            String name = removeNonAlphanumeric(levelName);
                            String description = "Rows" + removePeriods(levelName);
                            fieldDescriptions.put(name, description);
                        }
                    }

                    for (Position column : cellSet.getAxes().get(Axis.COLUMNS.axisOrdinal())) {
                        for (Member member : column.getMembers()) {
                            String memberName = member.getUniqueName();
                            String name = removeNonAlphanumeric(memberName);
                            String description = "Data(" + memberName + ",?)";
                            fieldDescriptions.put(name, description);
                        }
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot create field descriptions", e);
        }

        return fieldDescriptions;
    }

    private String removeNonAlphanumeric(String value) {
        return value.replaceAll("[\\W]", "");
    }

    private String removePeriods(String value) {
        return value.replaceAll("\\.", "");
    }

    private JRField createField(String name, String description, String type) {
        JRDesignField f = new JRDesignField();
        f.setName(name);
        f.setDescription(description);
        f.setValueClassName(type);
        return f;
    }

    public List<JRField> getFields() {
        return fields;
    }

    public JRDataSource getDataSource() throws JRException {
        JRDesignDataset dataSet = new JRDesignDataset(false);
        for (JRField f : fields) {
            dataSet.addField(f);
        }

        return new Olap4jDataSource(dataSet, cellSet);
    }

    public void setDirverClassName(String dirverClassName) {
        this.dirverClassName = dirverClassName;
    }

}
