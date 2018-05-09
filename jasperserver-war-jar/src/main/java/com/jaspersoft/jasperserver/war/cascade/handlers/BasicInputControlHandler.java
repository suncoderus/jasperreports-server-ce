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
package com.jaspersoft.jasperserver.war.cascade.handlers;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.dto.common.validations.DateTimeFormatValidationRule;
import com.jaspersoft.jasperserver.dto.common.validations.MandatoryValidationRule;
import com.jaspersoft.jasperserver.dto.common.validations.ValidationRule;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.dto.resources.ClientDataType;
import com.jaspersoft.jasperserver.war.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.InputControlValidationException;
import com.jaspersoft.jasperserver.war.cascade.handlers.converters.DataConverterService;
import com.jaspersoft.jasperserver.war.cascade.handlers.validators.InputControlValueValidator;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class BasicInputControlHandler implements InputControlHandler {
    /**
     * currently the only multi typed input control is control of type singleValue.
     * multyDataTypeMapping is used to determine UI type of control in this case
     */
    public static final String MULTI_DATA_TYPE_UI_TYPE = "singleValue";
    @Autowired
    private ApplicationContext applicationContext;
    @Resource
    private Map<String, String> multyDataTypeMapping;
    @Resource
    protected CachedRepositoryService cachedRepositoryService;
    @Resource
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;
    @Resource
    protected DataConverterService dataConverterService;
    @Resource
    protected MessageSource messageSource;
    @Resource(name = "isoCalendarFormatProvider")
    protected CalendarFormatProvider calendarFormatProvider;

    protected ToClientConverter<DataType, ClientDataType, ToClientConversionOptions> dataTypeResourceConverter;

    @PostConstruct
    public void init(){
        // this hint is needed in case of integration tests or import/export(command line) applicationContext.
        // There's no required converter in a context. But it's not used in these cases, it's used in web context only.
        // So, let's make this dependency optional.
        // This trick should be removed if jasperserver-remote module become part of test and import/export context
        try {
            dataTypeResourceConverter = applicationContext.getBean("dataTypeResourceConverter", ToClientConverter.class);
        }catch (NoSuchBeanDefinitionException e){
            // do nothing. Optional dependency
        }
    }

    @Override
    public void applyNothingSelected(String controlName, Map<String, Object> parameters) {
        parameters.remove(controlName);
    }

    @Override
    public boolean isNothingSelected(String[] rawData) {
        Boolean result = false;
        if (rawData != null) {
            List<String> rawDataList = Arrays.asList(rawData);
            result = rawDataList.contains(InputControlHandler.NOTHING_SUBSTITUTION_VALUE);
        }
        return result;
    }

    @Override
    public Set<String> getMasterDependencies(InputControl control, ResourceReference dataSource) throws CascadeResourceNotFoundException {
        // default implementation, should be overridden in child classes
        return new HashSet<String>();
    }

    @Override
    public ReportInputControl buildReportInputControl(InputControl inputControl, String uiType, ResourceReference dataSource) throws CascadeResourceNotFoundException {
        ReportInputControl control = new ReportInputControl();
        control.setId(inputControl.getName());
        control.setLabel(inputControl.getLabel());
        control.setDescription(inputControl.getDescription());
        control.setUri(inputControl.getURI());
        control.setMandatory(inputControl.isMandatory());
        control.setReadOnly(inputControl.isReadOnly());
        control.setVisible(inputControl.isVisible());
        control.setType(getType(inputControl, uiType));
        final List<ValidationRule> validationRules = getValidationRules(inputControl);
        if (validationRules != null && !validationRules.isEmpty()) {
            control.setValidationRules(validationRules);
        }
        final ResourceReference dataTypeReference = inputControl.getDataType();
        if(dataTypeReference != null){
            final DataType dataType = dataTypeReference.isLocal() ? (DataType) dataTypeReference.getLocalResource()
                    : cachedRepositoryService.getResource(DataType.class, dataTypeReference.getReferenceURI());
            if(dataType != null && dataTypeResourceConverter != null) {
                control.setDataType(dataTypeResourceConverter.toClient(dataType, ToClientConversionOptions.getDefault()));
            }
        }
        return control;
    }

    protected String getType(InputControl inputControl, String uiType) throws CascadeResourceNotFoundException {
        String type = uiType;
        if (MULTI_DATA_TYPE_UI_TYPE.equals(uiType)) {
            byte dataType = cachedRepositoryService.getResource(DataType.class, inputControl.getDataType()).getDataTypeType();
            type = multyDataTypeMapping.get(String.valueOf(dataType));
        }
        return type;

    }

    @Override
    public final InputControlState getState(InputControl inputControl, ResourceReference dataSource, Map<String, Object> parameters, Map<String, Class<?>> parameterTypes, ReportInputControlInformation info) throws CascadeResourceNotFoundException {
        InputControlState state = new InputControlState();
        state.setUri(inputControl.getURIString());
        state.setId(inputControl.getName());
        fillStateValue(state, inputControl, dataSource, parameters, info, parameterTypes);
        return state;
    }

    protected List<ValidationRule> getValidationRules(InputControl inputControl) throws CascadeResourceNotFoundException {
        List<ValidationRule> validationRules = new ArrayList<ValidationRule>();
        if (inputControl.isMandatory()) {
            final MandatoryValidationRule mandatoryValidationRule = new MandatoryValidationRule();
            mandatoryValidationRule.setErrorMessage(getMessage(MandatoryValidationRule.ERROR_KEY));
            validationRules.add(mandatoryValidationRule);
        }
        final DataType dataType = inputControl.getDataType() != null ? cachedRepositoryService.getResource(DataType.class, inputControl.getDataType()) : null;
        if(dataType != null){
            switch (dataType.getDataTypeType()){
                case DataType.TYPE_DATE:{
                    final DateTimeFormatValidationRule dateFormatValidationRule = new DateTimeFormatValidationRule();
                    dateFormatValidationRule.setFormat(calendarFormatProvider.getDatePattern());
                    dateFormatValidationRule.setErrorMessage(getMessage(DateTimeFormatValidationRule.INVALID_DATE));
                    validationRules.add(dateFormatValidationRule);
                }
                break;
                case DataType.TYPE_DATE_TIME:{
                    final DateTimeFormatValidationRule dateTimeFormatValidationRule = new DateTimeFormatValidationRule();
                    dateTimeFormatValidationRule.setFormat(calendarFormatProvider.getDatetimePattern());
                    dateTimeFormatValidationRule.setErrorMessage(getMessage(DateTimeFormatValidationRule.INVALID_DATE_TIME));
                    validationRules.add(dateTimeFormatValidationRule);
                }
                break;
                case DataType.TYPE_TIME:{
                    final DateTimeFormatValidationRule dateTimeFormatValidationRule = new DateTimeFormatValidationRule();
                    dateTimeFormatValidationRule.setFormat(calendarFormatProvider.getTimePattern());
                    dateTimeFormatValidationRule.setErrorMessage(getMessage(DateTimeFormatValidationRule.INVALID_TIME));
                    validationRules.add(dateTimeFormatValidationRule);
                }
                break;
            }
        }
        return validationRules;
    }

    protected void fillStateValue(InputControlState state, InputControl inputControl, ResourceReference dataSource, Map<String, Object> parameters, ReportInputControlInformation info, Map<String, Class<?>> parameterTypes) throws CascadeResourceNotFoundException {
        // call formatting of value in case if this control parameter present in input parameters. This is needed to avoid wrong null substitution
        if (parameters.containsKey(inputControl.getName())) {
            final Object typedValue = parameters.get(inputControl.getName());
            if (inputControl.isMandatory())
                doMandatoryValidation(typedValue, state);
            state.setValue(dataConverterService.formatSingleValue(typedValue, inputControl, info));
        } else
            internalApplyNothingSubstitution(inputControl.getName(), parameters);
    }

    /**
     * @param controlName
     * @param parameters
     * @deprecated this method need to be removed if real nothing substitution is implemented for single value and single select controls.
     */
    protected void internalApplyNothingSubstitution(String controlName, Map<String, Object> parameters) {
        parameters.put(controlName, null);
    }

    protected void doMandatoryValidation(Object typedValue, InputControlState state) {
        // if value isn't in error state and don't has a value, then put error message
        if (state.getError() == null && !hasValue(typedValue))
            state.setError(getMessage(MandatoryValidationRule.ERROR_KEY));
    }

    @Override
    public final Object convertParameterValueFromRawData(String[] rawData, InputControl inputControl, ReportInputControlInformation info) throws CascadeResourceNotFoundException, InputControlValidationException {
        // default value is used if no raw value provided
        Object value;
        if (rawData != null) {
            value = internalConvertParameterValueFromRawData(rawData, inputControl, info);
        } else {
            value = info.getDefaultValue();
        }

        internalValidateValue(value, inputControl, info);

        return value;
    }

    protected Boolean hasValue(Object value) {
        return value != null;
    }

    protected Object internalConvertParameterValueFromRawData(String[] rawData, InputControl inputControl, ReportInputControlInformation info) throws CascadeResourceNotFoundException, InputControlValidationException {
        return rawData != null && rawData.length > 0
                ? dataConverterService.convertSingleValue(rawData[0], inputControl, info) : null;
    }

    protected void internalValidateValue(Object value, InputControl inputControl, ReportInputControlInformation info) throws InputControlValidationException, CascadeResourceNotFoundException {
        final DataType dataType = inputControl.getDataType() != null ? cachedRepositoryService.getResource(DataType.class, inputControl.getDataType()) : null;
        try {
            validateSingleValue(value, dataType);
        } catch (InputControlValidationException e) {
            e.getValidationError().setInvalidValue(dataConverterService.formatSingleValue(value, inputControl, info));
            throw e;
        }
    }

    protected void validateSingleValue(Object value, DataType dataType) throws InputControlValidationException {
        if (value != null && dataType != null) {
            InputControlValueValidator<Object> validator = genericTypeProcessorRegistry.getTypeProcessor(value.getClass(), InputControlValueValidator.class, false);
            if (validator == null && value instanceof Comparable) {
                validator = genericTypeProcessorRegistry.getTypeProcessor(Comparable.class, InputControlValueValidator.class, false);
            }
            if (validator != null)
                validator.validateSingleValue(value, dataType);
        }
    }

    @Override
    public String[] formatValue(Object typedValue, InputControl inputControl, ReportInputControlInformation info) throws CascadeResourceNotFoundException {
        return new String[]{dataConverterService.formatSingleValue(typedValue, inputControl, info)};
    }

    protected String getMessage(String messageKey, Object... arguments) {
        return messageSource.getMessage(messageKey, arguments, LocaleContextHolder.getLocale());
    }
}
