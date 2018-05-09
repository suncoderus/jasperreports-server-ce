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
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import org.junit.Test;

import java.util.Map;

public class MultiSelectQueryInputControlHandlerNewTest extends InputControlHandlerBaseTest {

    @Test
    public void convertParameterValueFromRawDataRejectWrongNumber() throws Exception {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/" +
                "convertParameterValueFromRawDataWrongNumber-config.xml");

        String testCaseName = "convertParameterValueFromRawDataWrongNumber";

        InputControl control = getControl("order", testCaseName);
        ReportInputControlInformation info = getControlInfo("order", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_MULTI_SELECT_QUERY);
        String[] rawData = getRawDataArray(testCaseName);

        Object typedValues = inputControlHandler.convertParameterValueFromRawData(rawData, control, info);

        assertTypedValue("convertParameterValueFromRawDataWrongNumber", typedValues);
    }

    /**
     * Select none                                  | has one default, default is in query results     | mandatory     = select default
     */
    @Test
    public void getValue1() throws Exception {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/" +
                "multiSelectQueryInputControlHandler-fixtures-getValue1.xml");

        String testCaseName = "getValue1";

        InputControl control = getControl("country", testCaseName);
        ReportInputControlInformation info = getControlInfo("country", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_MULTI_SELECT_QUERY);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }

    /**
     * Select none                                  | has one default, default is in query results     | not mandatory = select default
     */
    @Test
    public void getValue2() throws Exception  {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/" +
                "multiSelectQueryInputControlHandler-fixtures-getValue2.xml");

        String testCaseName = "getValue2";

        InputControl control = getControl("country", testCaseName);
        ReportInputControlInformation info = getControlInfo("country", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_MULTI_SELECT_QUERY);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }

    /**
     * Select none                                  | has one default, default is not in query results | mandatory     = select first from query results
     */
    @Test
    public void getValue3() throws Exception  {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/" +
                "multiSelectQueryInputControlHandler-fixtures-getValue3.xml");

        String testCaseName = "getValue3";

        InputControl control = getControl("country", testCaseName);
        ReportInputControlInformation info = getControlInfo("country", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_MULTI_SELECT_QUERY);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }

    /**
     * Select Nothing in master single select control without default, slave multi select should return empty list.
     * @throws Exception
     */
    @Test
    public void getValue4() throws Exception  {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/" +
                "multiSelectQueryInputControlHandler-fixtures-getValue4.xml");

        String testCaseName = "getValue4";

        InputControl control = getControl("state", testCaseName);
        ReportInputControlInformation info = getControlInfo("state", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_MULTI_SELECT_QUERY);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }

    @Test
    public void getValueSomeDefaultValuesAreNotValidAgainstDataType() throws Exception  {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/" +
                "getValueSomeDefaultValuesAreNotValidAgainstDataType-config.xml");

        String testCaseName = "getValueSomeDefaultValuesAreNotValidAgainstDataType";

        InputControl control = getControl("order", testCaseName);
        ReportInputControlInformation info = getControlInfo("order", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_MULTI_SELECT_QUERY);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }

    @Test
    public void getValueSomeTypedParametersAreNotPresentIsResultSet() throws Exception  {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/" +
                "getValueSomeTypedParametersAreNotPresentIsResultSet-config.xml");

        String testCaseName = "getValueSomeTypedParametersAreNotPresentIsResultSet";

        InputControl control = getControl("order", testCaseName);
        ReportInputControlInformation info = getControlInfo("order", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_MULTI_SELECT_QUERY);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }

    @Test
    public void getValueSomeTypedParametersAreNotValidAgainstDataType() throws Exception  {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/" +
                "getValueSomeTypedParametersAreNotValidAgainstDataType-config.xml");

        String testCaseName = "getValueSomeTypedParametersAreNotValidAgainstDataType";

        InputControl control = getControl("order", testCaseName);
        ReportInputControlInformation info = getControlInfo("order", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_MULTI_SELECT_QUERY);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }

    @Test
    public void getValueCollectionParametersWithNoNestedType() throws Exception  {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/" +
                "getValueCollectionParametersWithNoNestedType-config.xml");

        String testCaseName = "getValueCollectionParametersWithNoNestedType";

        InputControl control = getControl("order", testCaseName);
        ReportInputControlInformation info = getControlInfo("order", testCaseName);

        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_MULTI_SELECT_QUERY);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }
}