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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.design.JRDesignField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: ichan
 * Date: 9/19/14
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTextDataSourceDefinition extends DataAdapterDefinition {

    protected static final Log log = LogFactory.getLog(DataAdapterDefinition.class);
    // searching column data type under the following order:
    protected Class<?> typeList[] = {java.util.Date.class, Boolean.class, Number.class, String.class};
    // assigning numeric data type under the following order:
    ArrayList<String> numericTypes = new ArrayList<String>(Arrays.asList("java.lang.Integer", "java.lang.Long", "java.lang.Double"));

    // FOR METADATA DISCOVERY
    int rowCountForMetadataDiscovery = -1;      // number of rows to use for metadata discovery

    public int getRowCountForMetadataDiscovery() {
        return rowCountForMetadataDiscovery;
    }

    public void setRowCountForMetadataDiscovery(int rowCountForMetadataDiscovery) {
        this.rowCountForMetadataDiscovery = rowCountForMetadataDiscovery;
    }

    public static boolean containsValue(List<?> list) {
        return list != null && (list.size() > 0);
    }

    /****  HELPER FUNCTIONS FOR FIELD NAMES DISCOVERY  ***/

    public static Map<String, String> getDefaultFieldMapping(List<String> fieldNames) {
        Map<String, String> fieldMapping = new HashMap<String, String>();
        if(fieldNames != null) {
            for (int i = 0; i < fieldNames.size(); i++) {
                fieldMapping.put(fieldNames.get(i), ("FIELD " + (i + 1)));
            }
        }
        return fieldMapping;
    }

    public static Map<String, String> getFieldMapping(List<String> fieldNames) {
        Map<String, String> fieldMapping = new HashMap<String, String>();
        if(fieldNames != null) {
            for (String fieldName : fieldNames) {
                fieldMapping.put(fieldName, fieldName);
            }
        }
        return fieldMapping;
    }

    public static boolean isValidFieldName(String fieldName) throws JRException {
        if (fieldName.contains(" ")) throw new JRException("Invalid field name: [" + fieldName + "] Space is not allowed in data source field");
        Pattern pattern = Pattern.compile("[-~#@*+%{}<>\\[\\]|\"\\^]");
        Matcher matcher = pattern.matcher(fieldName);
        if (matcher.find()) throw new JRException("Invalid field name: [" + fieldName + "] is not a valid name for data source field");
        return true;
    }

    // find field type from the data in JRDataSource
    protected List<String> getFieldTypes(JRDataSource dataSource, List<JRField> jrFields) {
        String columnTypeArray[] = new String[jrFields.size()];
        //scan through number of rows to use for metadata discovery
        for (int i = 0; (rowCountForMetadataDiscovery < 0) || (i < rowCountForMetadataDiscovery); i++) {
            for (int j = 0; j < jrFields.size(); j++) {
                if ((columnTypeArray[j] != null) && (columnTypeArray[j] == "java.lang.String")) continue;
                String type = findType(dataSource, (JRDesignField)jrFields.get(j));
                try {
                    columnTypeArray[j] = getCompatibleDataType(columnTypeArray[j], type);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Field \"" + jrFields.get(j).getName() + "\" contains mix data type [" + columnTypeArray[j] + "] and [" + type + "]");
                }
            }
            try {
                if (!dataSource.next()) break;
            } catch (Exception ex) {
                break;
            }
        }
        // default to String for all NULL columns
        for (int i = 0; i < columnTypeArray.length; i++) {
            if (columnTypeArray[i] == null) {
                columnTypeArray[i] = "java.lang.String";
            }
            log.debug("DETECTED COLUMN TYPE[" + i + "] = " + columnTypeArray[i]);
        }
        return Arrays.asList(columnTypeArray);
    }

    // get compatible data type
    // for example:  double, integer > double
    //               double, string  > string
    protected String getCompatibleDataType(String originalType, String newType) throws JRException {
        if (originalType == null) return newType;
        if (newType == null) return originalType;
        if (originalType.equals(newType)) return originalType;
        int originalNumericTypeIndex = numericTypes.indexOf(originalType);
        int newNumericTypeIndex = numericTypes.indexOf(newType);
        if ((originalNumericTypeIndex >= 0) && (newNumericTypeIndex >= 0)) {
            return numericTypes.get(Math.max(originalNumericTypeIndex, newNumericTypeIndex));
        }
        return "java.lang.String";
    }

    protected String findType(JRDataSource csvDataSource, JRDesignField field) {
        for (Class<?> type : typeList) {
            Class<?> fieldType = getFieldType(type);
            field.setValueClassName(fieldType.getName());
            field.setValueClass(fieldType);
            Object value = null;
            try {
                value = csvDataSource.getFieldValue(field);
            } catch (Exception ex) {
                continue;
            };
            if ((fieldType != String.class) && (value == null)) continue;
            if ((value == null) || value.toString().trim().equals("")) {
                // value is null.  Can't detect type
                return null;
            } else if (type == Boolean.class) {
                // BOOLEAN TYPE
                if (getBooleanType(value.toString()) == null) continue;
            } else if (type == Number.class) {
                // NUMERIC TYPE
                Class<?> numericType = getNumericType(value, field);
                if (numericType != null) type = numericType;
                else continue;

            }
            //    log.debug("FIELD = " + field.getName()  + " VALUE = " + value.toString() + ", TYPE = " + type.getName());
            return type.getName();
        }
        return "java.lang.String";
    }


    protected Class<?> getFieldType(Class<?> type) {
        if (type == Number.class || type == Boolean.class) return String.class;
        else return type;
    }

    /**
     * Converts the value into a number and returns the java type of the number.<br>
     * Specifics of this implementation:<br>
     * - returns {@link Double} as it's more accurate even if the value can be represented in {@link Float}<br>
     * - accepts only decimal representations<br>
     * - accepts only {@link String} and {@link Number} as the value<br>
     * - if the value is a {@link String} then accepts only decimal representations<br>
     * - doesn't accept java type qualifiers like 'L', 'd', etc... ('45L', '45f', etc...)
     * - doesn't accept hex values ('0xF22')
     * @param value - the value to convert
     * @return java type of which the value can be represented in or <code>null</code> if the value cannot be converted
     */
    protected Class<? extends Number> getNumericType(Object value, JRDesignField field) {
        Number number = null;

        if (value instanceof Number) {
            number = (Number) value;

        } else if (value instanceof String) {

            number = stringToNumber((String) value);
        }

        return number == null ? null : number.getClass();
    }

    private Number stringToNumber(String strValue) {
        if (StringUtils.isBlank(strValue)) {
            return null;
        }
        // don't accept values with a type qualifier ('45L', '45F', etc...)
        char lastDigit = strValue.charAt(strValue.length() - 1);
        if (!Character.isDigit(lastDigit)) {
            return null;
        }
        // don't accept hex values
        if (strValue.indexOf('x') > 0) {
            return null;
        }
        Number number = null;
        try {
            number = NumberUtils.createNumber(strValue);
        } catch (NumberFormatException e) {
            // ignored, returning null;
        }
        // special case of '.x' which NumberUtils evaluates to BigDecimal even if it fits double.
        if (number instanceof BigDecimal && number.doubleValue() == 0D) {
            number = new Double(number.doubleValue());
        }
        /*if (number != null &&
                (number instanceof Float || number instanceof Double) &&
                number.doubleValue() == Math.floor(number.doubleValue())) {
            number = new Integer(number.intValue());
        }*/
        // use double as it's more accurate
        return (number instanceof Float) ? new Double(number.doubleValue()) : number;
    }

    protected Class<?> getBooleanType(String value) {
        if (value.toString().equals("true") || value.toString().equals("false")) return Boolean.class;
        else return null;
    }

}
