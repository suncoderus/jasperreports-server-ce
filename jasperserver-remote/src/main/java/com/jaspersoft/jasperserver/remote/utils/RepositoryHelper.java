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
package com.jaspersoft.jasperserver.remote.utils;

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.util.LocaleHelper;
import com.jaspersoft.jasperserver.api.common.util.rd.DateRangeFactory;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.InvalidDateRangeExpressionException;
import net.sf.jasperreports.types.date.TimestampRange;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
/**
 *
 * @author gtoffoli
 * @version $Id$
 */
public class RepositoryHelper
{
    protected static final Log log = LogFactory.getLog(RepositoryHelper.class);
    
    /**
     * Converts BigDecimal numbers to the the type they should be based on
     * what the JRParameters say.
     *
     * @todo add float and dobule
     *
     * @param reportUri the name of the report
     * @param currentParameters parameters to fic
     */
    public static Map convertParameterValues(String reportUri, Map currentParameters, EngineService engine) {
		Map parametersMap = new HashMap();

		JRReport report = engine.getMainJasperReport(ExecutionContextImpl.getRuntimeExecutionContext(), reportUri);
		JRParameter[] parameters = report.getParameters();
		if (parameters != null)
		{
			for (int i = 0; i < parameters.length; i++)
			{
				JRParameter parameter = parameters[i];
				parametersMap.put(parameter.getName(), parameter);
			}
		}

		for (Iterator i = currentParameters.keySet().iterator(); i.hasNext();)
		{
			String parameterName = "" + i.next();

			JRParameter parameter = (JRParameter) parametersMap.get(parameterName);
			if (parameter != null)
			{
				Object value = currentParameters.get(parameterName);
				Object parameterValue;
				if (value == null)
				{
					parameterValue = null;
				}
				else if (value instanceof String)
				{
					parameterValue = stringToValue((String) value, parameter.getValueClass());
				}
				else if (value instanceof Collection)
				{
					parameterValue = getMultiParameterValues(parameter, (Collection) value);
				}
				else
				{
					parameterValue = value;
				}
				currentParameters.put(parameterName, parameterValue);
			}
		}

		return currentParameters;
	}

	protected static Object getMultiParameterValues(JRParameter parameter, Collection values) {
		Object parameterValue;
		Class parameterType = parameter.getValueClass();
		if (parameterType.equals(Object.class)
				|| parameterType.equals(Collection.class)
				|| parameterType.equals(Set.class)
				|| parameterType.equals(List.class)) {
				Collection paramValues;
				if (parameterType.equals(List.class)) {
					//if the parameter type is list, use a list
					paramValues = new ArrayList(values.size());
				} else {
					//else use an ordered set
					paramValues = new ListOrderedSet();
				}

				Class componentType = parameter.getNestedType();
				for (Iterator it = values.iterator(); it.hasNext();) {
					Object val = (Object) it.next();
					Object paramValue;
					if (componentType == null || !(val instanceof String)) {
						//no conversion if no nested type set for the parameter
						paramValue = val;
					} else {
						paramValue = stringToValue((String) val, componentType);
					}
					paramValues.add(paramValue);
				}
				parameterValue = paramValues;
			} else if (parameterType.isArray()) {
				Class componentType = parameterType.getComponentType();
				parameterValue = Array.newInstance(componentType, values.size());
				int idx = 0;
				for (Iterator iter = values.iterator(); iter.hasNext(); ++idx) {
					Object val = iter.next();
					Object paramValue;
					if (val instanceof String) {
						paramValue = stringToValue((String) val, componentType);
					} else {
						paramValue = val;
					}
					Array.set(parameterValue, idx, paramValue);
				}
			} else {
				parameterValue = values;
			}
		return parameterValue;
	}
    
    /**
     *  This method get a object and tries to convert it in a string
     *  Valid objects are:
     *    java.util.Date (converted in a string representing milliseconds)
     *    Number
     *    BigDecimal
     *    Byte
     *    Short
     *    Integer
     *    Long
     *    Float
     *    Double
     *    Boolean (will be the string true or false)
     *    String
     * 
     *  The default returned object is of type String
     * @param value The object to convert in a string
     * @return Return a string represeting the object
     */
    public static String valueToString(Object value)
    {
        String str = (value == null) ? "" : ""+value;
        if (value != null && value instanceof java.util.Date)
        {
            str = "" + ((java.util.Date)value).getTime();
        }
        
        return str;
    }
    
    
    /**
     *  This method get a string and tries to convert the string into the requested object.
     *  Valid classes are:
     *    java.util.Date (the string is supposed to represent a number of milliseconds)
     *    Number
     *    BigDecimal
     *    Byte
     *    Short
     *    Integer
     *    Long
     *    Float
     *    Double
     *    Boolean
     *    String
     *    java.util.Locale
     * 
     *  The default returned object is of type String
     * @param str the string representing the value
     * @param clazz The class of the result (not garanteed)
     * @return return an Object hopefully of the requested class (or a String)
     * 
     */
    public static Object stringToValue(String str, Class clazz)
    {
        Object value = str;
        if (value == null)
        {
        	return value;
        }
        
        if (java.util.Date.class.getName().equals(clazz.getName()))
        {
                value = new java.util.Date(Long.valueOf(value.toString()).longValue());
        }
        else if (java.sql.Date.class.getName().equals(clazz.getName()))
        {
                value = new java.sql.Date(Long.valueOf(value.toString()).longValue());
        }
        else if (java.sql.Timestamp.class.getName().equals(clazz.getName()))
        {
                value = new java.sql.Timestamp(Long.valueOf(value.toString()).longValue());
        }
        else if (Number.class.isAssignableFrom(clazz ))
        {
                value = new java.math.BigDecimal(value.toString());
                if (Byte.class.getName().equals(clazz.getName()))
                {
                        value = new Byte(((Number)value).byteValue());
                }
                else if (Short.class.getName().equals(clazz.getName()))
                {
                        value = new Short(((Number)value).shortValue());
                }
                else if (Integer.class.getName().equals(clazz.getName()))
                {
                        value = new Integer(((Number)value).intValue());
                }
                else if (Long.class.getName().equals(clazz.getName()))
                {
                        value = new Long(((Number)value).longValue());
                }
                else if (Float.class.getName().equals(clazz.getName()))
                {
                        value = new Float(((Number)value).floatValue());
                }
                else if (Double.class.getName().equals(clazz.getName()))
                {
                        value = new Double(((Number)value).doubleValue());
                }
        }
        else if (Boolean.class.getName().equals(clazz.getName()))
        {
                value = Boolean.valueOf(value.toString());
        }
        else if (Locale.class.getName().equals(clazz.getName()))
        {
        	value = LocaleHelper.getInstance().getLocale(str);
        } else if (DateRange.class.equals(clazz)) {
            try {
                value = DateRangeFactory.getInstance(str, Date.class);
            } catch (InvalidDateRangeExpressionException e) {
                //if DateRange passed as string with milliseconds we need to parse it
                Date dateValue = (Date)stringToValue(str, Date.class);
                value = DateRangeFactory.getInstance(dateValue);
            }
        } else if (TimestampRange.class.equals(clazz)) {
            try {
                value = DateRangeFactory.getInstance(str, Timestamp.class);
            } catch (InvalidDateRangeExpressionException e) {
                //if TimestampRange passed as string with milliseconds we need to parse it
                Timestamp timestamp = (Timestamp)stringToValue(str, Timestamp.class);
                value = DateRangeFactory.getInstance(timestamp);
            }
        }
        return value;
    }
    
    
}
