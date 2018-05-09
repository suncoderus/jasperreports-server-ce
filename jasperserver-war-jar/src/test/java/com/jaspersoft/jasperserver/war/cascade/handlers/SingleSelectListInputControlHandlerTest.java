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
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.war.cascade.handlers.converters.DataConverterService;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import org.apache.commons.collections.set.ListOrderedSet;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.entry;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.listOfValues;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.map;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.listOfOptions;
import static org.junit.Assert.*;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class SingleSelectListInputControlHandlerTest extends UnitilsJUnit4 {
    @TestedObject
    private SingleSelectListInputControlHandler handler;

    @InjectInto(property = "loader")
    private Mock<ValuesLoader> loader;

    @InjectInto(property = "dataConverterService")
    protected Mock<DataConverterService> dataConverterService;

    private Mock<InputControl> inputControlMock;

    private Mock<ReportInputControlInformation> reportInputControlInformationMock;

    /**
     * Incoming value is of type Integer.
     * List of values for input control contains values ot type BigDecimal.
     * Integer value has corresponding BigDecimal equivalent in list of values and corresponding option become selected.
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlNumberWithListOfBigDecimalAndIntegerValue() throws Exception {
        final String parameterName = "testName";
        // incoming value is of type Integer
        final Integer integerValue = Integer.valueOf(2);
        // BigDecimal equivalent of incoming value
        final BigDecimal bigDecimalValue = BigDecimal.valueOf(2);

        inputControlMock.returns(parameterName).getName();
        final InputControl inputControl = inputControlMock.getMock();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(entry(parameterName, integerValue));
        final ReportInputControlInformation info = reportInputControlInformationMock.getMock();

        // building list of values from BigDecimals
        final List<ListOfValuesItem> listOfValues = listOfValues(BigDecimal.valueOf(0), BigDecimal.valueOf(1), bigDecimalValue, BigDecimal.valueOf(3), BigDecimal.valueOf(4));
        loader.returns(listOfValues).loadValues(inputControl, null, inputParameters, parameterTypes, info);

        // dummy data conversion
        dataConverterService.returns(bigDecimalValue.toString()).formatSingleValue(bigDecimalValue, inputControl, info);

        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, info).getOptions();
        // state should have options
        assertNotNull(options);
        // options number is one item more then values because of nothing selected item.
        assertEquals(listOfValues.size() + 1, options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for (InputControlOption currentOption : options) {
            if (currentOption.isSelected()) selectedOptions.add(currentOption);
        }
        // only one item is selected
        assertEquals(selectedOptions.size(), 1);
        // selected item is string representation of BigDecimal, which is equivalent to incoming Integer value
        assertEquals(selectedOptions.get(0).getValue(), bigDecimalValue.toString());
    }

    /**
     * Incoming value is of type BigDecimal.
     * List of values for input control contains values ot type Integer.
     * BigDecimal value has corresponding Integer equivalent in list of values and corresponding option become selected.
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlNumberWithListOfIntegerAndBigDecimalValue() throws Exception {
        final String parameterName = "testName";
        // incoming value is of type BigDecimal
        final BigDecimal bigDecimalValue = BigDecimal.valueOf(3);
        // Integer equivalent of incoming value
        final Integer integerValue = Integer.valueOf(3);

        inputControlMock.returns(parameterName).getName();
        final InputControl inputControl = inputControlMock.getMock();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(entry(parameterName, bigDecimalValue));
        final ReportInputControlInformation info = reportInputControlInformationMock.getMock();
        // building list of values from Integers
        final List<ListOfValuesItem> listOfValues = listOfValues(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2), integerValue, Integer.valueOf(4));
        loader.returns(listOfValues).loadValues(inputControl, null, inputParameters, parameterTypes, info);
        // dummy data conversion
        dataConverterService.returns(integerValue.toString()).formatSingleValue(bigDecimalValue, inputControl, info);
        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, info).getOptions();
        // state should have options
        assertNotNull(options);
        // options number is one item more then values because of nothing selected item.
        assertEquals(listOfValues.size() + 1, options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for (InputControlOption currentOption : options) {
            if (currentOption.isSelected()) selectedOptions.add(currentOption);
        }
        // only one item is selected
        assertEquals(selectedOptions.size(), 1);
        // selected item is string representation of Integer, which is equivalent to incoming BigDecimal value
        assertEquals(selectedOptions.get(0).getValue(), integerValue.toString());
    }

    /**
     * Input control options ot type Long.
     * Checks if Numbers matcher works well for Long type. Covers the bugzilla #33225 case.
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlNumberWithListOfLong() throws Exception {
        final String parameterName = "testName";
        // Selected value is of type Long
        final Long selectedValue = Long.valueOf("49539595901085460");

        inputControlMock.returns(parameterName).getName();
        final InputControl inputControl = inputControlMock.getMock();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(entry(parameterName, selectedValue));
        final ReportInputControlInformation info = reportInputControlInformationMock.getMock();
        // building list of values from Longs
        final List<ListOfValuesItem> listOfValues = listOfValues(Long.valueOf("0"),Long.valueOf("49539595901085457"),
                Long.valueOf("49539595901085458"), Long.valueOf("49539595901085459"), selectedValue,  Long.valueOf("49539595901085461"));

        loader.returns(listOfValues).loadValues(inputControl, null, inputParameters, parameterTypes, info);

        dataConverterService.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                List<Object> args = proxyInvocation.getArguments();
                return args.get(0).toString();
            }
        }).formatSingleValue(null, null, info);

        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, info).getOptions();
        // state should have options
        assertNotNull(options);
        // options number is one item more then values because of nothing selected item.
        assertEquals(listOfValues.size() + 1, options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for (InputControlOption currentOption : options) {
            if (currentOption.isSelected()) selectedOptions.add(currentOption);
        }
        // only one item is selected
        assertEquals(selectedOptions.size(), 1);
        // selected item is string representation of Integer, which is equivalent to incoming BigDecimal value
        assertEquals(selectedOptions.get(0).getValue(), selectedValue.toString());
    }

    /**
     *  diagnostic datasnapshot contains IC default values
     *  chosen by user when run report under diagnostic
     *
     * @throws Exception
     */
    @Test
    public void getStateOfInputControlFromSingleDefaultValueOfDiagnosticDatasnapshot() throws Exception {
        final String parameterName = "testName";
        final String valueForDefault = "Diagnostic";

        final List<InputControlOption> defaultItems = new ArrayList<InputControlOption>() {{
            add(new InputControlOption(valueForDefault, valueForDefault, true));
        }};

        inputControlMock.returns(parameterName).getName();
        final InputControl inputControl = inputControlMock.getMock();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(
                entry(DiagnosticSnapshotPropertyHelper.ATTRIBUTE_IS_DIAG_SNAPSHOT, Boolean.TRUE),
                entry(parameterName, valueForDefault));

        reportInputControlInformationMock.returns(valueForDefault).getDefaultValue();
        final ReportInputControlInformation info = reportInputControlInformationMock.getMock();

        final List<ListOfValuesItem> listOfValues = listOfValues(
                Integer.valueOf(-2), Integer.valueOf(-1),
                Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));
        loader.returns(listOfValues).loadValues(inputControl, null, inputParameters, parameterTypes, info);

        dataConverterService.returns(valueForDefault).formatSingleValue(valueForDefault, inputControl, info);
        dataConverterService.returns(valueForDefault).convertSingleValue(valueForDefault, inputControl, info);
        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, info).getOptions();
        // state should have options
        assertNotNull(options);
        assertFalse("options not same list from db", options.size() == listOfValues.size() + 1);
        assertTrue("option same list of defaults", options.contains(defaultItems.get(0)));

        // options number is one item more then values because of nothing selected item.
        assertEquals(defaultItems.size()+1, options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for (InputControlOption currentOption : options) {
            if (currentOption.isSelected()) selectedOptions.add(currentOption);
        }
        // only one item is selected
        assertEquals(selectedOptions.size(), 1);
    }

    @Test
    public void getStateOfInputControlFromMultiDefaultValueOfDiagnosticDatasnapshot() throws Exception {
        final String parameterName = "testName";

        final List<String> valuesForDefault = new ArrayList<String>() {{
            add("Diag_01");
            add("Diag_02");
        }};

        final ListOrderedSet defaultICValues = new ListOrderedSet(){{
            addAll(valuesForDefault);
        }};

        final List<InputControlOption> defaultItems = listOfOptions(valuesForDefault, true);

        inputControlMock.returns(parameterName).getName();
        final InputControl inputControl = inputControlMock.getMock();
        final HashMap<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        Map<String, Object> inputParameters = map(
                entry(DiagnosticSnapshotPropertyHelper.ATTRIBUTE_IS_DIAG_SNAPSHOT, Boolean.TRUE),
                entry(parameterName, valuesForDefault.get(1)));

        reportInputControlInformationMock.returns(defaultICValues).getDefaultValue();
        final ReportInputControlInformation info = reportInputControlInformationMock.getMock();

        final List<ListOfValuesItem> listOfValues = listOfValues(
                Integer.valueOf(-2), Integer.valueOf(-1),
                Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));
        loader.returns(listOfValues).loadValues(inputControl, null, inputParameters, parameterTypes, info);

        // dummy data conversion
        dataConverterService.returns(valuesForDefault.get(0)).formatSingleValue(valuesForDefault.get(0), inputControl, info);
        dataConverterService.returns(valuesForDefault.get(1)).formatSingleValue(valuesForDefault.get(1), inputControl, info);
        dataConverterService.returns(valuesForDefault.get(0)).convertSingleValue(valuesForDefault.get(0), inputControl, info);
        dataConverterService.returns(valuesForDefault.get(1)).convertSingleValue(valuesForDefault.get(1), inputControl, info);
        ////////////tested method call/////////////////
        final List<InputControlOption> options = handler.getState(inputControl, null, inputParameters, parameterTypes, info).getOptions();
        // state should have options
        assertNotNull(options);
        assertFalse("options not same list from db", options.size() == listOfValues.size() + 1);
        assertTrue("option same list of defaults", options.contains(defaultItems.get(1)));

        // options number is one item more then values because of nothing selected item.
        assertEquals(defaultItems.size()+1, options.size());
        List<InputControlOption> selectedOptions = new ArrayList<InputControlOption>();
        for (InputControlOption currentOption : options) {
            if (currentOption.isSelected()) selectedOptions.add(currentOption);
        }
        // only one item is selected
        assertEquals(selectedOptions.size(), 1);
    }

}
