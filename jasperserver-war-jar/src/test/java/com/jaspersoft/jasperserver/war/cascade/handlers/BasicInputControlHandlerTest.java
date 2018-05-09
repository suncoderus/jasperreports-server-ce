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
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.InputControlImpl;
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
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.unitils.mock.ArgumentMatchers.eq;
import static org.unitils.mock.ArgumentMatchers.same;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class BasicInputControlHandlerTest  extends InputControlHandlerBaseTest {
    @TestedObject
    private BasicInputControlHandler handler;

    private PartialMock<BasicInputControlHandler> handlerPartialMock;

    @InjectInto(property = "cachedRepositoryService")
    private Mock<CachedRepositoryService> cachedRepositoryServiceMock;

    @InjectInto(property = "messageSource")
    private Mock<MessageSource> messageSourceMock;

    @InjectInto(property = "calendarFormatProvider")
    private Mock<CalendarFormatProvider> calendarFormatProviderMock;

    @InjectInto(property = "dataTypeResourceConverter")
    private Mock<ToClientConverter> dataTypeResourceConverter;

    @Before
    public void init() throws Exception{
        messageSourceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                // returns message code.
                return proxyInvocation.getArguments().get(0);
            }
        }).getMessage(null, null, null);
        cachedRepositoryServiceMock.resetBehavior();
        dataTypeResourceConverter.resetBehavior();
    }

    @Test
    public void buildReportInputControl_localDataType() throws CascadeResourceNotFoundException {
        final InputControlImpl inputControl = new InputControlImpl();
        final DataTypeImpl dataType = new DataTypeImpl();
        inputControl.setDataType(dataType);
        final ClientDataType clientDataType = new ClientDataType().setMaxValue("100").setMinValue("10")
                .setStrictMax(true).setStrictMin(true);
        dataTypeResourceConverter.returns(clientDataType).toClient(dataType, ToClientConversionOptions.getDefault());
        final ReportInputControl result = handler.buildReportInputControl(inputControl, null, null);
        assertNotNull(result);
        assertEquals(clientDataType, result.getDataType());
    }

    @Test
    public void buildReportInputControl_referenceDataType() throws CascadeResourceNotFoundException {
        final InputControlImpl inputControl = new InputControlImpl();
        final DataTypeImpl dataType = new DataTypeImpl();
        final String referenceUri = "/test/resource/reference";
        inputControl.setDataTypeReference(referenceUri);
        final ClientDataType clientDataType = new ClientDataType().setMaxValue("100").setMinValue("10")
                .setStrictMax(true).setStrictMin(true);
        // usage of ArgumentMatcher here somehow conflicts with mockito. I have no idea why, but the only way to fix it
        // was to specify behaviour in a way below avoiding argument matchers
        dataTypeResourceConverter.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                final List<Object> arguments = proxyInvocation.getArguments();
                return arguments.get(0) == dataType ? clientDataType : null;
            }
        }).toClient(dataType, ToClientConversionOptions.getDefault());
        cachedRepositoryServiceMock.returns(dataType).getResource(same(DataType.class), eq(referenceUri));
        final ReportInputControl result = handler.buildReportInputControl(inputControl, null, null);
        assertNotNull(result);
        assertEquals(clientDataType, result.getDataType());
    }

    @Test
    public void getValidationRules_mandatory_nonmandatory() throws Exception{
        InputControl inputControl = new InputControlImpl();
        inputControl.setMandatory(true);
        List<ValidationRule> validationRules = handler.getValidationRules(inputControl);
        assertNotNull(validationRules);
        Assert.assertFalse(validationRules.isEmpty());
        MandatoryValidationRule mandatoryValidationRule = getValidationRule(validationRules, MandatoryValidationRule.class);
        assertNotNull(mandatoryValidationRule);
        // mocked message source returns error code as result message
        Assert.assertEquals(MandatoryValidationRule.ERROR_KEY, mandatoryValidationRule.getErrorMessage());
        // check case for nonmandatory
        inputControl.setMandatory(false);
        validationRules = handler.getValidationRules(inputControl);
        mandatoryValidationRule = getValidationRule(validationRules, MandatoryValidationRule.class);
        Assert.assertNull(mandatoryValidationRule);
    }

    private <T extends ValidationRule> T getValidationRule(List<ValidationRule> rules, Class<T> concreteRuleType){
        T result = null;
        if(rules != null && !rules.isEmpty()){
            for(ValidationRule currentRule : rules){
                if(concreteRuleType.isAssignableFrom(currentRule.getClass())){
                    result = (T) currentRule;
                    break;
                }
            }
        }
        return result;
    }

    private <T extends ValidationRule> List<T> getValidationRules(List<ValidationRule> rules, Class<T> concreteRuleType){
        List<T> result = new ArrayList<T>();
        if(rules != null && !rules.isEmpty()){
            for(ValidationRule currentRule : rules){
                if(concreteRuleType.isAssignableFrom(currentRule.getClass())){
                    result.add((T) currentRule);
                }
            }
        }
        return result;
    }

    @Test
    public void getValidationRules_date_datetime() throws Exception{
        InputControl inputControl = new InputControlImpl();
        inputControl.setDataType(new ResourceReference((String)null));
        DataType dataType = new DataTypeImpl();
        cachedRepositoryServiceMock.returns(dataType).getResource(null, (ResourceReference) null);
        final String expectedDatePattern = "expectedDatePattern";
        final String expectedDateTimePattern = "expectedDateTimePattern";
        final String expectedTimePattern = "expectedTimePattern";
        calendarFormatProviderMock.returns(expectedDatePattern).getDatePattern();
        calendarFormatProviderMock.returns(expectedDateTimePattern).getDatetimePattern();
        calendarFormatProviderMock.returns(expectedTimePattern).getTimePattern();
        // case with type date
        dataType.setDataTypeType(DataType.TYPE_DATE);
        List<ValidationRule> validationRules = handler.getValidationRules(inputControl);
        List<DateTimeFormatValidationRule> rules = getValidationRules(validationRules, DateTimeFormatValidationRule.class);
        assertNotNull(rules);
        Assert.assertTrue(rules.size() == 1);
        DateTimeFormatValidationRule rule = rules.get(0);
        assertNotNull(rule);
        Assert.assertEquals(DateTimeFormatValidationRule.INVALID_DATE, rule.getErrorMessage());
        Assert.assertEquals(expectedDatePattern, rule.getFormat());
        // case with type datetime
        dataType.setDataTypeType(DataType.TYPE_DATE_TIME);
        validationRules = handler.getValidationRules(inputControl);
        rules = getValidationRules(validationRules, DateTimeFormatValidationRule.class);
        assertNotNull(rules);
        Assert.assertTrue(rules.size() == 1);
        rule = rules.get(0);
        assertNotNull(rule);
        Assert.assertEquals(DateTimeFormatValidationRule.INVALID_DATE_TIME, rule.getErrorMessage());
        Assert.assertEquals(expectedDateTimePattern, rule.getFormat());
        // case with type time
        dataType.setDataTypeType(DataType.TYPE_TIME);
        validationRules = handler.getValidationRules(inputControl);
        rules = getValidationRules(validationRules, DateTimeFormatValidationRule.class);
        assertNotNull(rules);
        Assert.assertTrue(rules.size() == 1);
        rule = rules.get(0);
        assertNotNull(rule);
        Assert.assertEquals(DateTimeFormatValidationRule.INVALID_TIME, rule.getErrorMessage());
        Assert.assertEquals(expectedTimePattern, rule.getFormat());
    }

    @Test
    public void buildReportInputControl_getValidationRules_getType_invoked() throws Exception{
        final String expectedType = "expectedType";
        final InputControl inputControl = new InputControlImpl();
        inputControl.setURIString("/test/uri/string");
        final String uiType = "testUiType";
        final List<ValidationRule> validationRules = new ArrayList<ValidationRule>();
        validationRules.add(new MandatoryValidationRule());
        handlerPartialMock.returns(expectedType).getType(inputControl, uiType);
        handlerPartialMock.returns(validationRules).getValidationRules(inputControl);
        final ReportInputControl reportInputControl = handlerPartialMock.getMock().buildReportInputControl(inputControl, uiType, null);
        assertNotNull(reportInputControl);
        Assert.assertSame(validationRules, reportInputControl.getValidationRules());
        Assert.assertEquals(expectedType, reportInputControl.getType());
        Assert.assertEquals(inputControl.getURI(), reportInputControl.getUri());
    }

    @Test
    public void convertParameterValueFromRawDataNullAndDefaultNull() throws Exception {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/basicInputControlHandler/" +
                "verifyNullSubstitutionWhenNoInputParamsAndDefaultIsNull.xml");

        String testCaseName = "verifyNullSubstitutionWhenNoInputParamsAndDefaultIsNull";

        InputControl control = getControl("String", testCaseName);
        ReportInputControlInformation info = getControlInfo("String", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_SINGLE_VALUE);
        String[] rawData = getRawDataArray(testCaseName);

        Object typedValues = inputControlHandler.convertParameterValueFromRawData(rawData, control, info);

        assertTypedValue("verifyNullSubstitutionWhenNoInputParamsAndDefaultIsNull", typedValues);
    }

    @Test
    public void getValueNullSubstitutionTypedParamsNullDefaultNull() throws Exception  {
        setApplicationContextForTestCase("classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/basicInputControlHandler/" +
                "getValueWhenTypedParamNullDefaultNull.xml");

        String testCaseName = "getValueWhenTypedParamNullDefaultNull";

        InputControl control = getControl("String", testCaseName);
        ReportInputControlInformation info = getControlInfo("String", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_SINGLE_VALUE);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }
}
