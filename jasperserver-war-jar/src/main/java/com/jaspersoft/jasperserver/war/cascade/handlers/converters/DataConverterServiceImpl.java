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
package com.jaspersoft.jasperserver.war.cascade.handlers.converters;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.war.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.InputControlValidationException;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

import static com.jaspersoft.jasperserver.war.cascade.handlers.converters.InputControlValueClassResolver.getValueClass;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
@Service("dataConverterService")
public class DataConverterServiceImpl implements DataConverterService {

    @Resource
    protected CachedRepositoryService cachedRepositoryService;
    @Resource
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;
    @Resource
    protected MessageSource messageSource;
    @Resource
    private Map<Byte, String> globalDefaultValues;

    private <T> DataConverter<T> getDataConverter(Class<T> valueClass) {
        return genericTypeProcessorRegistry.getTypeProcessor(valueClass, DataConverter.class);
    }

    private DateParser<?> getDateParser(Class<?> valueClass) {
        return genericTypeProcessorRegistry.getTypeProcessor(valueClass, DateParser.class, false);
    }


    @Override
    public String formatSingleValue(Object typedValue, InputControl inputControl, ReportInputControlInformation info)
            throws CascadeResourceNotFoundException {
        final DataType dataType = getDataType(inputControl);
        return formatSingleValue(typedValue, dataType, getValueClass(dataType, info));
    }

    public Object getDefaultValueForDataType(DataType dataType){
        String configuredValue = globalDefaultValues.get(dataType != null ? dataType.getDataTypeType() : -1);
        String nullSubstitutionLessValue = null;
        try {
            // use StringDataConverter to process null substitution.
            // If configured value is InputControlHandler.NULL_SUBSTITUTION_VALUE, then nullSubstitutionLessValue will be null
            nullSubstitutionLessValue = getDataConverter(String.class).stringToValue(configuredValue);
        } catch (Exception e) {
            throw new JSException(e);
        }
        return nullSubstitutionLessValue != null ? convertSingleValue(nullSubstitutionLessValue, dataType) : null;
    }

    @Override
    public String formatSingleValue(Object typedValue) {
        return formatSingleValue(typedValue, (DataType)null, null);
    }

    @Override
    // DataConverter generic parameter type compatibility is checked inside of data converters registry. So, cast is safe
    @SuppressWarnings("unchecked")
    public String formatSingleValue(Object typedValue, DataType dataType, Class<?> defaultClass){

        Class<?> valueClass;

        // Do the trick here - use default class if is assignable from value class.
        if (typedValue != null) {
            if (defaultClass != null && defaultClass.isAssignableFrom(typedValue.getClass())) {
                valueClass = defaultClass;
            } else {
                valueClass = typedValue.getClass();
            }
        } else {
            valueClass = defaultClass;
        }
        String result;
        // For special case when JRParameter class is Timestamp, but control data type is Date
        if (dataType != null && DataType.TYPE_DATE == dataType.getDataTypeType() && getDateParser(valueClass) != null) {
            result = ((DateParser) getDateParser(valueClass)).dateToString(typedValue);
        } else {
            result = ((DataConverter) getDataConverter(valueClass)).valueToString(typedValue);
        }
        return result;
    }

    @Override
    public Object convertSingleValue(String rawValue, InputControl inputControl, ReportInputControlInformation info)
            throws CascadeResourceNotFoundException, InputControlValidationException {
        final DataType dataType = getDataType(inputControl);
        return convertSingleValue(rawValue, dataType, getValueClass(dataType, info));
    }

    public Object convertSingleValue(String rawValue, DataType dataType) throws InputControlValidationException {
        return convertSingleValue(rawValue, dataType, null);
    }

    @Override
    public Object convertSingleValue(String rawValue, DataType dataType, Class<?> inputValueClass)
            throws InputControlValidationException {

        Class<?> valueClass = inputValueClass != null ? inputValueClass : getValueClass(dataType, null);

        Object result;
        // value should be null if null substitution is received or if value class isn't String (in this case empty sting also means null)
        try {
            // For special case when JRParameter class is Timestamp, but control data type is Date
            if (dataType != null && DataType.TYPE_DATE == dataType.getDataTypeType() && getDateParser(valueClass) != null) {
                result = getDateParser(valueClass).parsDate(rawValue);
            } else {
                result = getDataConverter(valueClass).stringToValue(rawValue);
            }
        } catch (Exception e) {
            final String className = valueClass.getSimpleName();
            String message = messageSource.getMessage(
                    "fillParameters.error.invalidValueForType", new Object[]{className}, LocaleContextHolder.getLocale());

            throw new InputControlValidationException(
                    "fillParameters.error.invalidValueForType",
                    new Object[]{className},
                    message, rawValue);
        }
        return result;
    }

    private DataType getDataType(InputControl inputControl) throws CascadeResourceNotFoundException {
        return (inputControl != null && inputControl.getDataType() != null)
                ? cachedRepositoryService.getResource(DataType.class, inputControl.getDataType()) : null;
    }
}
