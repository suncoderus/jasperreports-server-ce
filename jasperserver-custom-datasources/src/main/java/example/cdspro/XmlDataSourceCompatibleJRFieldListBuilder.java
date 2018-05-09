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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.AbstractXmlDataSource;
import net.sf.jasperreports.engine.design.JRDesignField;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * Builds a list of JR fields. Fields in resulting list are ordered in the order they are added to the builder.
 * The fields are to be used in {@link AbstractXmlDataSource}. Because of that following rules are used:
 * <ul>
 * <li>multiple fields with the same name are renamed to field_1, field_2, etc...
 * <li>for fields with unique name description is set to field name
 * <li>for fields with non-unique name description is set to field[1], field[2], etc...
 * </ul>
 * @author Renat Zhilkibaev
 */
class XmlDataSourceCompatibleJRFieldListBuilder {

    /**
     * Maps field names to number of occurrences of this field.
     */
    private final Map<String, MutableInt> name2CountMap = new LinkedHashMap<String, MutableInt>();
    private final Map<String, String> labelMap = new LinkedHashMap<String, String>();
    /**
     * Adds a new field with provided name. The field will have name based on the uniqueness of the name.
     * @param name the suggested name for the field
     * @return this builder
     */
    public XmlDataSourceCompatibleJRFieldListBuilder field(String name) {
        return field(name, null);
    }

    public XmlDataSourceCompatibleJRFieldListBuilder field(String name, String label) {
        checkInput(name);
        MutableInt count = getCountForName(name);
        count.increment();
        name2CountMap.put(name, count);
        if (label != null) labelMap.put(name, label);
        return this;
    }

    private void checkInput(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Field name cannot be blank");
        }
    }

    private MutableInt getCountForName(String name) {
        MutableInt count = name2CountMap.get(name);
        return count == null ? new MutableInt() : count;
    }

    /**
     * Creates a list of string type fields with name and description set based on this builder state.
     * A new instance of {@link ArrayList} is returned for each invocation of this method.
     * @return a list of fields, never <code>null</code>
     */
    public List<JRField> build() {
        List<JRField> fields = new ArrayList<JRField>();
        for (Map.Entry<String, MutableInt> entry : name2CountMap.entrySet()) {
            String name = entry.getKey();
            MutableInt count = entry.getValue();
            if (count.intValue() == 1) {
                fields.add(createJRField(getLabel(name), name));
            } else {
                for (int i = 1; i <= count.intValue(); i++) {
                    fields.add(createJRField(getLabel(name) + "_" + i, name + "[" + i + "]"));
                }
            }
        }
        return fields;
    }

    private String getLabel(String name) {
        String label = labelMap.get(name);
        if (label != null) return label;
        return name;
    }

    private JRField createJRField(String label, String description) {
        JRDesignField f = new JRDesignField();
        f.setName(label);
        f.setDescription(description);
        f.setValueClassName(String.class.getName());
        return f;
    }
}
