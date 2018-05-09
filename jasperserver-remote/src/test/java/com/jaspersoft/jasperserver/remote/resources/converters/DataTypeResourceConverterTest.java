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
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientDataType;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.war.cascade.InputControlValidationException;
import com.jaspersoft.jasperserver.war.cascade.handlers.converters.DataConverterService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class DataTypeResourceConverterTest {
    @InjectMocks
    private DataTypeResourceConverter converter = new DataTypeResourceConverter();
    @Mock
    private DataConverterService dataConverterService;

    @BeforeMethod
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeHelper.extractClientType(ClientDataType.class));
        assertEquals(converter.getServerResourceType(), DataType.class.getName());
    }

    @Test
    public void resourceSpecificFieldsToServer_text() throws Exception {
        final String expectedPattern = "testPattern";
        final Boolean expectedStrictMax = true;
        final Boolean expectedStrictMin = true;
        final Integer expectedMaxLength = 125;
        final Integer expectedDecimals = 5;
        DataType expectedServerObject = new DataTypeImpl();
        ClientDataType clientObject = new ClientDataType();
        clientObject.setType(ClientDataType.TypeOfDataType.text);
        clientObject.setPattern(expectedPattern);
        clientObject.setStrictMax(expectedStrictMax);
        clientObject.setStrictMin(expectedStrictMin);
        clientObject.setMaxLength(expectedMaxLength);
        final DataType result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertSame(result, expectedServerObject);
        assertEquals(result.getRegularExpr(), expectedPattern);
        assertEquals(result.isStrictMax(), false);
        assertEquals(result.isStrictMin(), false);
        assertEquals(result.getMaxLength(), expectedMaxLength);
        assertEquals(result.getDecimals(), null);
    }

    @Test
    public void resourceSpecificFieldsToServer_number() throws Exception {
        final String expectedPattern = "testPattern";
        final Boolean expectedStrictMax = true;
        final Boolean expectedStrictMin = true;
        final Integer expectedMaxLength = 125;
        final Integer expectedDecimals = 5;
        DataType expectedServerObject = new DataTypeImpl();
        ClientDataType clientObject = new ClientDataType();
        clientObject.setType(ClientDataType.TypeOfDataType.number);
        clientObject.setPattern(expectedPattern);
        clientObject.setStrictMax(expectedStrictMax);
        clientObject.setStrictMin(expectedStrictMin);
        clientObject.setMaxLength(expectedMaxLength);
        final DataType result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertSame(result, expectedServerObject);
        assertEquals(result.getRegularExpr(), null);
        assertEquals(result.isStrictMax(), expectedStrictMax.booleanValue());
        assertEquals(result.isStrictMin(), expectedStrictMin.booleanValue());
        assertEquals(result.getMaxLength(), null);
    }

    @Test
    public void resourceSpecificFieldsToServer_date() throws Exception {
        final String expectedPattern = "testPattern";
        final Boolean expectedStrictMax = true;
        final Boolean expectedStrictMin = true;
        final Integer expectedMaxLength = 125;
        final Integer expectedDecimals = 5;
        DataType expectedServerObject = new DataTypeImpl();
        ClientDataType clientObject = new ClientDataType();
        clientObject.setType(ClientDataType.TypeOfDataType.date);
        clientObject.setPattern(expectedPattern);
        clientObject.setStrictMax(expectedStrictMax);
        clientObject.setStrictMin(expectedStrictMin);
        clientObject.setMaxLength(expectedMaxLength);
        final DataType result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertSame(result, expectedServerObject);
        assertEquals(result.getRegularExpr(), null);
        assertEquals(result.isStrictMax(), expectedStrictMax.booleanValue());
        assertEquals(result.isStrictMin(), expectedStrictMin.booleanValue());
        assertEquals(result.getMaxLength(), null);
        assertEquals(result.getDecimals(), null);
    }

    @Test
    public void resourceSpecificFieldsToServer_datetime() throws Exception {
        final String expectedPattern = "testPattern";
        final Boolean expectedStrictMax = true;
        final Boolean expectedStrictMin = true;
        final Integer expectedMaxLength = 125;
        final Integer expectedDecimals = 5;
        DataType expectedServerObject = new DataTypeImpl();
        ClientDataType clientObject = new ClientDataType();
        clientObject.setType(ClientDataType.TypeOfDataType.datetime);
        clientObject.setPattern(expectedPattern);
        clientObject.setStrictMax(expectedStrictMax);
        clientObject.setStrictMin(expectedStrictMin);
        clientObject.setMaxLength(expectedMaxLength);
        final DataType result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertSame(result, expectedServerObject);
        assertEquals(result.getRegularExpr(), null);
        assertEquals(result.isStrictMax(), expectedStrictMax.booleanValue());
        assertEquals(result.isStrictMin(), expectedStrictMin.booleanValue());
        assertEquals(result.getMaxLength(), null);
        assertEquals(result.getDecimals(), null);
    }

    @Test
    public void resourceSpecificFieldsToServer_time() throws Exception {
        final String expectedPattern = "testPattern";
        final Boolean expectedStrictMax = true;
        final Boolean expectedStrictMin = true;
        final Integer expectedMaxLength = 125;
        final Integer expectedDecimals = 5;
        DataType expectedServerObject = new DataTypeImpl();
        ClientDataType clientObject = new ClientDataType();
        clientObject.setType(ClientDataType.TypeOfDataType.time);
        clientObject.setPattern(expectedPattern);
        clientObject.setStrictMax(expectedStrictMax);
        clientObject.setStrictMin(expectedStrictMin);
        clientObject.setMaxLength(expectedMaxLength);
        final DataType result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertSame(result, expectedServerObject);
        assertEquals(result.getRegularExpr(), null);
        assertEquals(result.isStrictMax(), expectedStrictMax.booleanValue());
        assertEquals(result.isStrictMin(), expectedStrictMin.booleanValue());
        assertEquals(result.getMaxLength(), null);
        assertEquals(result.getDecimals(), null);
    }

    @Test
    public void resourceSpecificFieldsToServer_type() throws Exception {
        ClientDataType clientObject = new ClientDataType();
        DataType expectedServerObject = new DataTypeImpl();
        // ignore by default
        clientObject.setType(null);
        DataType result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertEquals(result.getDataTypeType(), 0);
        // number
        clientObject.setType(ClientDataType.TypeOfDataType.number);
        result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertEquals(result.getDataTypeType(), DataType.TYPE_NUMBER);
        // text
        clientObject.setType(ClientDataType.TypeOfDataType.text);
        result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertEquals(result.getDataTypeType(), DataType.TYPE_TEXT);
        // date
        clientObject.setType(ClientDataType.TypeOfDataType.date);
        result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertEquals(result.getDataTypeType(), DataType.TYPE_DATE);
        // datetime
        clientObject.setType(ClientDataType.TypeOfDataType.datetime);
        result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertEquals(result.getDataTypeType(), DataType.TYPE_DATE_TIME);
        // time
        clientObject.setType(ClientDataType.TypeOfDataType.time);
        result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertEquals(result.getDataTypeType(), DataType.TYPE_TIME);
    }

    @Test
    public void resourceSpecificFieldsToServer_maxValue_minValue() throws InputControlValidationException, IllegalParameterValueException {
        final String expectedMaxValue = "testMaxValue";
        final String expectedMinValue = "testMinValue";
        final String maxValueToConvert = "testMaxValueToConvert";
        final String minValueToConvert = "testMinValueToConvert";
        final String invalidMaxValue = "textInvalidMaxValue";
        final String invalidMinValue = "textInvalidMinValue";
        final DataType expectedServerObject = new DataTypeImpl();
        final ClientDataType clientObject = new ClientDataType();
        clientObject.setType(ClientDataType.TypeOfDataType.number);
        clientObject.setMaxValue(maxValueToConvert);
        clientObject.setMinValue(minValueToConvert);
        when(dataConverterService.convertSingleValue(maxValueToConvert, expectedServerObject)).thenReturn(expectedMaxValue);
        when(dataConverterService.convertSingleValue(minValueToConvert, expectedServerObject)).thenReturn(expectedMinValue);
        when(dataConverterService.convertSingleValue(invalidMaxValue,expectedServerObject)).thenThrow(new InputControlValidationException(null, null, null, null));
        when(dataConverterService.convertSingleValue(invalidMinValue,expectedServerObject)).thenThrow(new InputControlValidationException(null, null, null, null));
        final DataType result = converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        assertSame(result, expectedServerObject);
        assertEquals(result.getMaxValue(), expectedMaxValue);
        assertEquals(result.getMinValue(), expectedMinValue);
        IllegalParameterValueException exception = null;
        clientObject.setMaxValue(invalidMaxValue);
        try{
            converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        } catch (IllegalParameterValueException ex){
            exception = ex;
        }
        assertNotNull(exception);
        exception = null;
        clientObject.setMaxValue(null);
        clientObject.setMaxValue(invalidMinValue);
        try{
            converter.resourceSpecificFieldsToServer(clientObject, expectedServerObject, null);
        } catch (IllegalParameterValueException ex){
            exception = ex;
        }
        assertNotNull(exception);
    }

    @Test
    public void resourceSpecificFieldsToClient(){
        final String expectedPattern = "testPattern";
        final Boolean expectedStrictMax = true;
        final Boolean expectedStrictMin = true;
        final Integer expectedMaxLength = 125;
        final Integer expectedDecimals = 5;
        DataType serverObject = new DataTypeImpl();
        ClientDataType expectedClientObject = new ClientDataType();
        serverObject.setRegularExpr(expectedPattern);
        serverObject.setStrictMax(expectedStrictMax);
        serverObject.setStrictMin(expectedStrictMin);
        serverObject.setMaxLength(expectedMaxLength);
        serverObject.setDecimals(expectedDecimals);
        final ClientDataType result = converter.resourceSpecificFieldsToClient(expectedClientObject, serverObject, null);
        assertSame(result, expectedClientObject);
        assertEquals(result.getPattern(), expectedPattern);
        assertEquals(result.isStrictMax(), expectedStrictMax);
        assertEquals(result.isStrictMin(), expectedStrictMin);
        assertEquals(result.getMaxLength(), expectedMaxLength);
    }

    @Test
    public void resourceSpecificFieldsToClient_emptyStringAsPatternIsIgnored(){
        DataType serverObject = new DataTypeImpl();
        ClientDataType expectedClientObject = new ClientDataType();
        serverObject.setRegularExpr("");
        final ClientDataType result = converter.resourceSpecificFieldsToClient(expectedClientObject, serverObject, null);
        assertSame(result, expectedClientObject);
        assertNull(result.getPattern());
    }

    @Test
    public void resourceSpecificFieldsToClient_type() throws Exception {
        ClientDataType clientObject = new ClientDataType();
        DataType serverObject = new DataTypeImpl();
        // number
        serverObject.setDataTypeType(DataType.TYPE_NUMBER);
        ClientDataType result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertEquals(result.getType(), ClientDataType.TypeOfDataType.number);
        // text
        serverObject.setDataTypeType(DataType.TYPE_TEXT);
        result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertEquals(result.getType(), ClientDataType.TypeOfDataType.text);
        // date
        serverObject.setDataTypeType(DataType.TYPE_DATE);
        result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertEquals(result.getType(), ClientDataType.TypeOfDataType.date);
        // datetime
        serverObject.setDataTypeType(DataType.TYPE_DATE_TIME);
        result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertEquals(result.getType(), ClientDataType.TypeOfDataType.datetime);
        // time
        serverObject.setDataTypeType(DataType.TYPE_TIME);
        result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertEquals(result.getType(), ClientDataType.TypeOfDataType.time);
        // invalid type
        Exception exception = null;
        serverObject.setDataTypeType((byte) 10);
        try{
            converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        } catch (Exception e){
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void resourceSpecificFieldsToClient_maxValue_minValue() throws Exception {
        final String expectedMaxValue = "testMaxValue";
        final String expectedMinValue = "testMinValue";
        final String maxValueToConvert = "testMaxValueToConvert";
        final String minValueToConvert = "testMinValueToConvert";
        final DataType serverObject = new DataTypeImpl();
        final ClientDataType expectedClientObject = new ClientDataType();
        serverObject.setMaxValue(maxValueToConvert);
        serverObject.setMinValue(minValueToConvert);
        when(dataConverterService.formatSingleValue(maxValueToConvert, serverObject, String.class)).thenReturn(expectedMaxValue);
        when(dataConverterService.formatSingleValue(minValueToConvert, serverObject, String.class)).thenReturn(expectedMinValue);
        final ClientDataType result = converter.resourceSpecificFieldsToClient(expectedClientObject, serverObject, null);
        assertSame(result, expectedClientObject);
        assertEquals(result.getMaxValue(), expectedMaxValue);
        assertEquals(result.getMinValue(), expectedMinValue);
    }

}
