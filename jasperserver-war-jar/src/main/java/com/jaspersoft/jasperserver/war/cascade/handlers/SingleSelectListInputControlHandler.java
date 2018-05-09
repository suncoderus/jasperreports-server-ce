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

import com.jaspersoft.jasperserver.api.common.util.diagnostic.DiagnosticSnapshotPropertyHelper;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.InputControlValidationException;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.MessageFormat;
import java.util.*;

import static com.jaspersoft.jasperserver.war.cascade.handlers.NumberUtils.toBigDecimal;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class SingleSelectListInputControlHandler extends BasicInputControlHandler {

    protected final static Log log = LogFactory.getLog(SingleSelectListInputControlHandler.class);


    private ValuesLoader loader;

    public ValuesLoader getLoader() {
        return loader;
    }

    public void setLoader(ValuesLoader loader) {
        this.loader = loader;
    }

    @Override
    public ReportInputControl buildReportInputControl(InputControl inputControl, String uiType, ResourceReference dataSource) throws CascadeResourceNotFoundException {
        final ReportInputControl reportInputControl = super.buildReportInputControl(inputControl, uiType, dataSource);
        final Set<String> masterDependencies = loader.getMasterDependencies(inputControl, dataSource);
        if (masterDependencies != null)
            reportInputControl.getMasterDependencies().addAll(masterDependencies);
        return reportInputControl;
    }

    @Override
    public Set<String> getMasterDependencies(InputControl control, ResourceReference dataSource) throws CascadeResourceNotFoundException {
        return loader.getMasterDependencies(control, dataSource);
    }

    @Override
    protected Object internalConvertParameterValueFromRawData(String[] rawData, InputControl inputControl, ReportInputControlInformation info) throws CascadeResourceNotFoundException, InputControlValidationException {
        Object value;
        try {
            value = super.internalConvertParameterValueFromRawData(rawData, inputControl, info);
        } catch (InputControlValidationException e) {
            // Explicitly set value for single select list to Null, thus we will recognize later that
            // this error doesn't need to be added to input control state
            e.getValidationError().setInvalidValue(null);
            throw e;
        }
        return value;
    }

    @Override
    protected void internalValidateValue(Object value, InputControl inputControl, ReportInputControlInformation info) throws InputControlValidationException, CascadeResourceNotFoundException {
        final DataType dataType = inputControl.getDataType() != null ? cachedRepositoryService.getResource(DataType.class, inputControl.getDataType()) : null;
        try {
            validateSingleValue(value, dataType);
        } catch (InputControlValidationException e) {
            // Explicitly set value for single select list to Null, thus we will recognize later that
            // this error doesn't need to be added to input control state
            e.getValidationError().setInvalidValue(null);
            throw e;
        }
    }

    /**
     * @param state        - state object to fill
     * @param inputControl - input control
     * @param dataSource   - data source
     * @param parameters   - incoming parameters
     * @param info         - input control information (contains input control default value)
     * @param parameterTypes
     * @throws CascadeResourceNotFoundException
     *          in case if some required for cascade logic resource isn't found.
     */
    @Override
    protected void fillStateValue(InputControlState state, InputControl inputControl, ResourceReference dataSource, Map<String, Object> parameters, ReportInputControlInformation info, Map<String, Class<?>> parameterTypes) throws CascadeResourceNotFoundException {
        List<ListOfValuesItem> values = null;
        if (DiagnosticSnapshotPropertyHelper.isDiagSnapshotSet(parameters)) {
            //  If datasnapshot is available then default values for IC
            // will be loaded from datasnapshot parameters, this logic is counting on that.
            values = generateValuesFromDefaultValues(inputControl, info);
            inputControl.setReadOnly(true);
        } else {
            values = loader.loadValues(inputControl, dataSource, parameters, parameterTypes, info);
        }
        final DataType dataType = inputControl.getDataType() != null ? cachedRepositoryService.getResource(DataType.class, inputControl.getDataType()) : null;
        List<InputControlOption> options = new ArrayList<InputControlOption>();
        InputControlOption nothingOption = null;
        if (shouldAddNothinSelectedOption(inputControl)) {
            nothingOption = buildInputControlOption(NOTHING_SUBSTITUTION_LABEL, NOTHING_SUBSTITUTION_VALUE);
            options.add(nothingOption);
        }
        // default options will be collected to defaultOptions map
        // default values are collected to a map because of option object and typed value are both required
        Map<InputControlOption, Object> defaultOptions = new HashMap<InputControlOption, Object>();
        // selected values will be collected to selectedValues list
        List<Object> selectedValues = new ArrayList<Object>();
        final String controlName = inputControl.getName();
        Boolean isNothingSelected = isNothingSelected(controlName, parameters);
        Object incomingValue = parameters.get(controlName);
        Object defaultValue = info.getDefaultValue();
        if (values != null && !values.isEmpty()) {
            // iterate over values
            for (ListOfValuesItem currentItem : values) {
                Object currentItemValue = currentItem.getValue();
                if (currentItemValue instanceof String) {
                    // if incoming value is a String, then convert it to corresponding Object for correct further comparison
                    try {
                        currentItemValue = dataConverterService.convertSingleValue((String) currentItemValue, inputControl, info);
                    } catch (InputControlValidationException e) {
                        // this exception doesn't depend on user's input. So, throw technical runtime exception
                        throw new IllegalStateException(e.getValidationError().getDefaultMessage());
                    }
                }

                boolean optionIsValid = true;
                try {
                    validateSingleValue(currentItem.getValue(), dataType);
                } catch (InputControlValidationException e) {
                    optionIsValid = false;
                }
                // Add option only if it meets validation rules of the dataType
                if (optionIsValid) {
                    InputControlOption option = buildInputControlOption(currentItem.getLabel(), dataConverterService.formatSingleValue(currentItemValue, inputControl, info));
                    Boolean isSelected = !isNothingSelected && matches(currentItemValue, incomingValue);
                    option.setSelected(isSelected);
                    if (isSelected) {
                        // collect selected values to update parameters with selected data
                        selectedValues.add(currentItemValue);
                    }
                    // collect default options if no selected values found and not a case that nothing is selected
                    if (selectedValues.size() == 0 && !isNothingSelected && matches(currentItemValue, defaultValue)) {
                        // collect default options and values if no incoming values found yet
                        defaultOptions.put(option, currentItemValue);
                    }
                    options.add(option);
                }
            }
        }
        if (selectedValues.size() == 0 && !options.isEmpty()) {
            // incoming value isn't found in values list
            if (!defaultOptions.isEmpty()) {
                // default values found. So, they should be selected
                for (InputControlOption defaultOption : defaultOptions.keySet())
                    defaultOption.setSelected(true);
                // put default values to selected values list for further update of incoming parameters map
                selectedValues = new ArrayList<Object>(defaultOptions.values());
            } else if (inputControl.isMandatory() && !isNothingSelected) {
                // default values sre not found in values list
                // this control is mandatory, first value should be selected
                options.get(0).setSelected(true);
                selectedValues.add(values.get(0).getValue());
            }
        }
        state.setOptions(options);
        // update incoming parameters map with selected values
        if (selectedValues.size() == 0) {
            if (inputControl.isMandatory())
                doMandatoryValidation(null, state);// mandatory error should appears in this case
            // if control had some value but this value isn't in options (i.e. no such value in result set), then applyNothingSelected() should be called here too.
            applyNothingSelected(controlName, parameters);
            if (nothingOption != null)
                nothingOption.setSelected(true);
            internalApplyNothingSubstitution(controlName, parameters);
        } else {
            // selected values are exist, update incoming parameters
            parameters.put(controlName, getConcreteSelectedValue(selectedValues));
        }
    }

    private List<ListOfValuesItem> generateValuesFromDefaultValues(
            InputControl inputControl, ReportInputControlInformation info) {
        List<ListOfValuesItem> result = new ArrayList<ListOfValuesItem>();
        List<Object> mid;
        if (info.getDefaultValue() instanceof Collection<?>) {
            mid =  ((ListOrderedSet)info.getDefaultValue()).asList();
        } else {
            mid = Arrays.asList(new Object[] {info.getDefaultValue()});
        }
        Object value;
        String formattedValue;
        for (int i = 0; i < mid.size(); i++) {
            value = mid.get(i);
            if (null != value) {
                formattedValue=value.toString();
            } else {
                // just for tolerance ...
                formattedValue="Null";
            }
            ListOfValuesItem item = new ListOfValuesItemImpl();
            item.setLabel(formattedValue);
            item.setValue(formattedValue);
            result.add(item);
        }
        return result;
    }


    protected Boolean shouldAddNothinSelectedOption(InputControl inputControl) {
        return !inputControl.isMandatory();
    }

    protected Boolean isNothingSelected(String inputControlName, Map<String, Object> incomingParameters) {
        return !incomingParameters.containsKey(inputControlName);
    }

    /**
     * @param selectedValues list of selected values
     * @return first item of selected values list (singleselect)
     */
    protected Object getConcreteSelectedValue(List<Object> selectedValues) {
        return selectedValues != null && !selectedValues.isEmpty() ? selectedValues.get(0) : null;
    }

    /**
     * Check if given value is selected
     *
     * @param valueToCheck value to check
     * @param selected     selected value
     * @return true if both values are null or equals
     */
    protected Boolean matches(Object valueToCheck, Object selected) {

        // areBothNulls
        if (valueToCheck == null && selected == null) {
            return true;
        };

        boolean isEqualityByStringFormats = false;
        if (selected != null) {

            // isDirectEquality
            if (selected.equals(valueToCheck)){
                return true;
            };

            try {
                if (valueToCheck != null) {

                    // isWorkaroudForNumbers
                    if (selected instanceof Number
                            && valueToCheck instanceof Number
                            && toBigDecimal((Number) valueToCheck).compareTo(toBigDecimal((Number) selected)) == 0
                        ) {
                        return true;
                    };

                    // isEqualityByStringFormats
                    String valueToCheckInStringFormat = dataConverterService.formatSingleValue(valueToCheck, null, (Class) null);
                    String selectedInStringFormat = dataConverterService.formatSingleValue(selected, null, (Class) null);
                    if (valueToCheckInStringFormat != null && selectedInStringFormat != null) {
                        isEqualityByStringFormats = selectedInStringFormat.equals(valueToCheckInStringFormat);
                    }
                }
            } catch (IllegalStateException e){
                log.warn(MessageFormat.format(
                        "Can't convert incoming value type '{0}'  or  selected value type '{1}' to string format. Add converters to register",
                        valueToCheck != null ? valueToCheck.getClass() : valueToCheck,
                        selected != null ? selected.getClass() : selected)
                );
            }
        }
        return isEqualityByStringFormats;
    }

    protected InputControlOption buildInputControlOption(String label, String value) {
        return new InputControlOption(value, label);
    }
}
