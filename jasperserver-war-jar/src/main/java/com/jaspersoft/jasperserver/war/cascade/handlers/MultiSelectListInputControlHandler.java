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
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.InputControlValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class MultiSelectListInputControlHandler extends SingleSelectListInputControlHandler {

    @Override
    protected Object internalConvertParameterValueFromRawData(String[] rawData, InputControl inputControl, ReportInputControlInformation info) throws CascadeResourceNotFoundException, InputControlValidationException {
        Object result = null;
        if (rawData != null) {
            List<Object> values = new ArrayList<Object>();
            for (String currentRawValue : rawData) {
                try {
                    values.add(dataConverterService.convertSingleValue(currentRawValue, inputControl, info));
                } catch (InputControlValidationException e) {
                    // Do not add this value to the list
                }
            }
            result = values;
        }
        return result;
    }

    @Override
    protected void internalValidateValue(Object value, InputControl inputControl, ReportInputControlInformation info) throws InputControlValidationException, CascadeResourceNotFoundException {
        if (value instanceof Collection) {
            final Collection<Object> collection = (Collection<Object>) value;
            List<Object> invalidValues = new ArrayList<Object>();
            for (Object o : collection) {
                try {
                    super.internalValidateValue(o, inputControl, info);
                } catch (InputControlValidationException e) {
                    invalidValues.add(o);
                }
            }
            // Reject invalid values from list
            collection.removeAll(invalidValues);
        }
    }

    @Override
    protected Boolean hasValue(Object value) {
        return value instanceof Collection<?> && !((Collection<?>) value).isEmpty();
    }

    @Override
    public String[] formatValue(Object typedValue, InputControl control, ReportInputControlInformation info) throws CascadeResourceNotFoundException {
        if (typedValue instanceof Collection) {
            @SuppressWarnings("unchecked")
            final Collection<Object> values = (Collection) typedValue;
            List<String> strings = new ArrayList<String>();
            for (Object currentValue : values) {
                strings.add(dataConverterService.formatSingleValue(currentValue, control, info));
            }
            return strings.toArray(new String[strings.size()]);
        } else if (typedValue == null) {
            return new String[0];
        } else {
            throw new IllegalArgumentException(new StringBuilder("Value for MultiSelect control cannot be ")
                    .append(typedValue.getClass().getName()).append(", it should be a Collection.").toString());
        }
    }

    /**
     * Check if selected values collection contains given value
     *
     * @param valueToCheck value to check
     * @param selected     selected value
     * @return true if selected values collection contains given value
     */
    protected Boolean matches(Object valueToCheck, Object selected) {
        Boolean result = false;
        if(selected instanceof Collection<?>){
            Collection<?> selectedValues = (Collection<?>) selected;
            for (Object currentSelectedValue : selectedValues){
                if(super.matches(valueToCheck, currentSelectedValue)){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    protected Boolean isNothingSelected(String inputControlName, Map<String, Object> incomingParameters) {
        Object incomingValue = incomingParameters.get(inputControlName);
        return (incomingValue instanceof Collection<?>) && (((Collection<?>) incomingValue).isEmpty());
    }

    @Override
    public boolean isNothingSelected(String[] rawData) {
        //Still need to keep [~NOTHING~] notation for mulstiselect in order to allow
        //nothing selection set for parameter in URL
        return (rawData != null && rawData.length == 0) || super.isNothingSelected(rawData);
    }

    @Override
    public void applyNothingSelected(String controlName, Map<String, Object> parameters) {
        parameters.put(controlName, Collections.emptyList());
    }

    @Override
    protected Boolean shouldAddNothinSelectedOption(InputControl inputControl) {
        return false;
    }

    /**
     * @param selectedValues list of selected values
     * @return whole list of values as value (multiselect)
     */
    @Override
    protected Object getConcreteSelectedValue(List<Object> selectedValues) {
        return selectedValues;
    }

    @Override
    protected void internalApplyNothingSubstitution(String controlName, Map<String, Object> parameters) {
        // Multiselect input control has full support of nothing selection.
        // Therefore this handler don't need to perform any actions in this deprecated method
    }
}

