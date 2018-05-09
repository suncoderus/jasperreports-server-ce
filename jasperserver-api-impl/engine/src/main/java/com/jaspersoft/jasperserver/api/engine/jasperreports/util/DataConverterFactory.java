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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DataConverterFactory {

    private static Map converterMap;
    private static Map pumpUpDataTypeMap;

    public interface DataConverter {

         public Object convert(Object object);

    }

    static {
        converterMap = new HashMap();
        converterMap.put("java.lang.String", "StringConverter");
        converterMap.put("java.lang.Byte",  "ByteConverter");
        converterMap.put("java.lang.Short",  "ShortConverter");
        converterMap.put("java.lang.Integer", "IntegerConverter");
        converterMap.put("java.lang.Float", "FloatConverter");
        converterMap.put("java.lang.Double", "DoubleConverter");
        converterMap.put("java.lang.Number", "DoubleConverter");
        converterMap.put("java.util.Date", "DateConverter");

        pumpUpDataTypeMap = new HashMap();
        pumpUpDataTypeMap.put("java.lang.Byte",  "java.lang.Long");
        pumpUpDataTypeMap.put("java.lang.Short",  "java.lang.Long");
        pumpUpDataTypeMap.put("java.lang.Integer", "java.lang.Long");
        pumpUpDataTypeMap.put("java.lang.Float", "java.lang.Double");
        pumpUpDataTypeMap.put("java.lang.Number", "java.lang.Double");
    }

    public static String getPumpUpType(String originalType) {
         return (String) pumpUpDataTypeMap.get(originalType);
    }

    public static DataConverter createConverter(String type) {
        String converterClass = (String) converterMap.get(type);
        if (converterClass == null) return null;
        converterClass = "com.jaspersoft.jasperserver.api.engine.jasperreports.util.DataConverterFactory$" + converterClass;
        try {
            return (DataConverter) Class.forName(converterClass).newInstance();
        } catch (Exception e) {
            // if no converter available, returns null
            return null;
        }
    }


    public static class StringConverter implements DataConverter {

        public Object convert(Object value) {
            if (value == null) {
                return null;
            }
            return value.toString();
        }

    }

    public static class DateConverter implements DataConverter {

        public Object convert(Object value) {
            if (value == null || value.getClass().equals(getType())) {
                return (Date) value;
            } else if (value instanceof Date) {
                // it'd better be some kind of date
                long time = ((Date) value).getTime();
                return new Date(time);
            } else {
                throw new IllegalArgumentException("passed value of class " + value.getClass().getName() + " to DateColumn.set(), expecting subclass of java.util.Date");
            }
        }

        public Class getType() {
            return java.util.Date.class;
        }

    }
    
    public static class FloatConverter implements DataConverter {

        public Object convert(Object value) {
            if (value == null || value.getClass().equals(getType())) {
                return (Float) value;
            } else if (value instanceof Number) {
                // it'd better be some kind of number
                float f = ((Number) value).floatValue();
                return new Float(f);
            } else {
                throw new IllegalArgumentException("passed value of class " + value.getClass().getName() + " to FloatColumn.set(), expecting subclass of Number");
            }
        }

        public Class getType() {
            return Float.class;
        }
    }
    
    public static class DoubleConverter implements DataConverter {
        
        public Object convert(Object value) {
            if (value == null || value.getClass().equals(getType())) {
                return (Double) value;
            } else if (value instanceof Number) {
                // it'd better be some kind of number
                double d = ((Number) value).doubleValue();
                return new Double(d);
            } else {
                throw new IllegalArgumentException("passed value of class " + value.getClass().getName() + " to DoubleColumn.set(), expecting subclass of Number");
            }
        }

        public Class getType() {
            return Double.class;
        }

    }
    
    public static class IntegerConverter implements DataConverter {

        public Object convert(Object value) {
            if (value == null || value.getClass().equals(getType())) {
                return (Integer) value;
            } else if (value instanceof Number) {
                // it'd better be some kind of number
                int i = ((Number) value).intValue();
                return new Integer(i);
            } else {
                throw new IllegalArgumentException("passed value of class " + value.getClass().getName() + " to IntegerColumn.set(), expecting subclass of Number");
            }
        }

        public Class getType() {
            return Integer.class;
        }

    }
    
    public static class ShortConverter implements DataConverter {
        
        public Object convert(Object value) {
            if (value == null || value.getClass().equals(getType())) {
                return (Short) value;
            } else if (value instanceof Number) {
                // it'd better be some kind of number
                short s = ((Number) value).shortValue();
                return new Short(s);
            } else {
                throw new IllegalArgumentException("passed value of class " + value.getClass().getName() + " to ShortColumn.set(), expecting subclass of Number");
            }
        }

        public Class getType() {
            return Short.class;
        }

    }
    
    public static class ByteConverter implements DataConverter {

        public Object convert(Object value) {
            if (value == null || value.getClass().equals(getType())) {
                return (Byte) value;
            } else if (value instanceof Number) {
                // it'd better be some kind of number
                byte b = ((Number) value).byteValue();
                return Byte.valueOf(b);
            } else {
                throw new IllegalArgumentException("passed value of class " + value.getClass().getName() + " to ShortColumn.set(), expecting subclass of Number");
            }
        }

        public Class getType() {
            return Byte.class;
        }

    }
    
    public static class LongAdapter implements DataConverter {
        
        public Object convert(Object value) {
            if (value == null || value.getClass().equals(getType())) {
                return (Long) value;
            } else if (value instanceof Number) {
                // it'd better be some kind of number
                long l = ((Number) value).longValue();
                return new Long(l);
            } else {
                throw new IllegalArgumentException("passed value of class " + value.getClass().getName() + " to LongColumn.set(), expecting subclass of Number");
            }
        }

        public Class getType() {
            return Long.class;
        }

    }

}
