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
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.war.cascade.handlers.converters.DataConverterService;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.entry;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.list;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.listOfValues;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class MultiSelectListInputControlHandlerTest extends UnitilsJUnit4 {

    @TestedObject
    private MultiSelectListInputControlHandler handler;

    @InjectInto(property = "loader")
    private Mock<ValuesLoader> loader;

    @InjectInto(property = "dataConverterService")
    protected Mock<DataConverterService> dataConverterService;

    private Mock<InputControl> inputControlMock;

    private Mock<ReportInputControlInformation> reportInputControlInformationMock;

    /**
     * Incoming value is of type Collection<Integer>.
     * List of values for input control contains values ot type BigDecimal.
     * Each Integer value has corresponding BigDecimal equivalent in list of values and corresponding options become selected.
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlNumberWithListOfBigDecimalAndIntegerValues() throws Exception {
        final String parameterName = "testName";
        // incoming values is a Collection of Integer items
        final Collection<Integer> integerValues = list(Integer.valueOf(2), Integer.valueOf(5), Integer.valueOf(7));
        // string representation of integers
        final Collection<String> selectedValuesToCheck = list("2", "5", "7");

        // BigDecimal equivalent of incoming integers
        final BigDecimal bigDecimalValue2 = BigDecimal.valueOf(2);
        final BigDecimal bigDecimalValue5 = BigDecimal.valueOf(5);
        final BigDecimal bigDecimalValue7 = BigDecimal.valueOf(7);

        inputControlMock.returns(parameterName).getName();
        final InputControl inputControl = inputControlMock.getMock();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(entry(parameterName, integerValues));
        final ReportInputControlInformation info = reportInputControlInformationMock.getMock();
        // building list of values from BigDecimals
        final List<ListOfValuesItem> listOfValues = listOfValues(BigDecimal.valueOf(0), BigDecimal.valueOf(1), bigDecimalValue2, BigDecimal.valueOf(3), BigDecimal.valueOf(4), bigDecimalValue5, BigDecimal.valueOf(6), bigDecimalValue7);
        loader.returns(listOfValues).loadValues(inputControl, null, inputParameters, parameterTypes, info);
        // dummy data conversion
        dataConverterService.returns(bigDecimalValue2.toString()).formatSingleValue(bigDecimalValue2, inputControl, info);
        dataConverterService.returns(bigDecimalValue5.toString()).formatSingleValue(bigDecimalValue5, inputControl, info);
        dataConverterService.returns(bigDecimalValue7.toString()).formatSingleValue(bigDecimalValue7, inputControl, info);
        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, info).getOptions();
        // state should have options
        assertNotNull(options);
        // options count equals to listOfValues items count
        assertEquals(listOfValues.size(), options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for(InputControlOption currentOption : options){
            if(currentOption.isSelected()) {
                selectedOptions.add(currentOption);
                // selected option value is one of predefined values to be selected
                assertTrue(selectedValuesToCheck.contains(currentOption.getValue()));
            }
        }
        // selected options count equals to incoming integers count
        assertEquals(selectedOptions.size(), integerValues.size());
    }

    /**
     * Incoming value is of type Collection<BigDecimal>.
     * List of values for input control contains values ot type Integer.
     * Each BigDecimal value has corresponding Integer equivalent in list of values and corresponding options become selected.
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlNumberWithListOfIntegerAndBigDecimalValues() throws Exception {
        final String parameterName = "testName";
        // incoming values is a Collection of BigDecimal items
        final Collection<BigDecimal> bigDecimalValues = list(BigDecimal.valueOf(3), BigDecimal.valueOf(4), BigDecimal.valueOf(6));
        // string representation of integers
        final Collection<String> selectedValuesToCheck = list("3", "4", "6");

        // Integer equivalent of incoming integers
        final Integer integerValue3 = Integer.valueOf(3);
        final Integer integerValue4 = Integer.valueOf(4);
        final Integer integerValue6 = Integer.valueOf(6);

        inputControlMock.returns(parameterName).getName();
        final InputControl inputControl = inputControlMock.getMock();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(entry(parameterName, bigDecimalValues));
        final ReportInputControlInformation info = reportInputControlInformationMock.getMock();

        // building list of values from Integers
        final List<ListOfValuesItem> listOfValues = listOfValues(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2), integerValue3, integerValue4, Integer.valueOf(5), integerValue6, Integer.valueOf(7), Integer.valueOf(8));
        loader.returns(listOfValues).loadValues(inputControl, null, inputParameters, parameterTypes, info);
        // dummy data conversion
        dataConverterService.returns(integerValue3.toString()).formatSingleValue(integerValue3, inputControl, info);
        dataConverterService.returns(integerValue4.toString()).formatSingleValue(integerValue4, inputControl, info);
        dataConverterService.returns(integerValue6.toString()).formatSingleValue(integerValue6, inputControl, info);

        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, info).getOptions();
        // state should have options
        assertNotNull(options);
        // options count equals to listOfValues items count
        assertEquals(listOfValues.size(), options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for(InputControlOption currentOption : options){
            if(currentOption.isSelected()) {
                selectedOptions.add(currentOption);
                // selected option value is one of predefined values to be selected
                assertTrue(selectedValuesToCheck.contains(currentOption.getValue()));
            }
        }
        // selected options count equals to incoming BigDecimals count
        assertEquals(selectedOptions.size(), bigDecimalValues.size());
    }

}
