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

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.war.action.EngineServiceCascadeTestQueryExecutor;
import com.jaspersoft.jasperserver.war.cascade.CachedEngineService;
import com.jaspersoft.jasperserver.war.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.InputControlValidationException;
import com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import org.apache.commons.collections.OrderedMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.jaspersoft.jasperserver.war.cascade.handlers.InputControlHandler.NULL_SUBSTITUTION_LABEL;
import static com.jaspersoft.jasperserver.war.cascade.handlers.InputControlHandler.NULL_SUBSTITUTION_VALUE;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.*;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createCalendarFormatProvider;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createEngineService;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createFilterResolver;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createMessageSource;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.setUpApplicationContext;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Anton Fomin
 * @version $Id$
 */
public class MultiSelectQueryInputControlHandlerTest extends UnitilsJUnit4 {

    private Mock<ResourceReference> dataSource;

    private Mock<CachedEngineService> cachedEngineService;

    private CalendarFormatProvider calendarFormatProvider;

    private Mock<MessageSource> messageSource;

    private ParametersHelper ph;

    @Test
    public void convertParameterValueFromRawDataNullDefaultValueNull() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = null;
        Object defaultValue = null;

        ph.setDefaultParameterValue(CASCADE_COUNTRY, defaultValue);
        ph.setMandatory(CASCADE_COUNTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertNull(convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataNull() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = null;
        Object defaultValue = list("USA", "Mexico", "Canada");
        Object expectedValue = list("USA", "Mexico", "Canada");

        ph.setDefaultParameterValue(CASCADE_COUNTRY, defaultValue);
        ph.setMandatory(CASCADE_COUNTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertEquals(expectedValue, convertedValue);
    }

    // Not real case, because we suppose to not receive empty array, but either [""] OR ["~NOTHING~"].
    // However, it's good to have such test in order to know how the code behaves.
    @Test
    public void convertParameterValueFromRawDataEmpty() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {};
        Object defaultValue = list("USA", "Mexico", "Canada");
        Object expectedValue = list();

        ph.setDefaultParameterValue(CASCADE_COUNTRY, defaultValue);
        ph.setMandatory(CASCADE_COUNTRY, false);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertEquals(expectedValue, convertedValue);
    }

    // Currently Nothing isn't handled by convertParameterValueFromRawData, let's show this.
    @Test
    public void convertParameterValueFromRawDataNothingMandatory() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {InputControlHandler.NOTHING_SUBSTITUTION_VALUE};
        Object defaultValue = list("USA", "Mexico", "Canada");
        Object expectedValue = list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE);

        ph.setDefaultParameterValue(CASCADE_COUNTRY, defaultValue);
        ph.setMandatory(CASCADE_COUNTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertEquals(expectedValue, convertedValue);
    }

    // Currently Nothing isn't handled by convertParameterValueFromRawData, let's show this.
    @Test
    public void convertParameterValueFromRawDataNothingNotMandatory() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {InputControlHandler.NOTHING_SUBSTITUTION_VALUE};
        Object defaultValue = list("USA", "Mexico", "Canada");
        Object expectedValue = list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE);

        ph.setDefaultParameterValue(CASCADE_COUNTRY, defaultValue);
        ph.setMandatory(CASCADE_COUNTRY, false);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataNormal() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {"Ukraine", "Romania"};
        Object defaultValue = list("USA", "Mexico", "Canada");
        Object expectedValue = list("Ukraine", "Romania");

        ph.setDefaultParameterValue(CASCADE_COUNTRY, defaultValue);
        ph.setMandatory(CASCADE_COUNTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataNullSubstitution() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {NULL_SUBSTITUTION_VALUE, "Ukraine", "Romania"};
        Object defaultValue = list("USA", "Mexico", "Canada");
        Object expectedValue = list(null, "Ukraine", "Romania");

        ph.setDefaultParameterValue(CASCADE_COUNTRY, defaultValue);
        ph.setMandatory(CASCADE_COUNTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataOnlyNullSubstitution() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {NULL_SUBSTITUTION_VALUE};
        Object defaultValue = list("USA", "Mexico", "Canada");
        Object expectedValue = nullList();

        ph.setDefaultParameterValue(CASCADE_COUNTRY, defaultValue);
        ph.setMandatory(CASCADE_COUNTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataDefaultNull() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {"USA"};
        Object defaultValue = null;
        Object expectedValue = list("USA");

        ph.setDefaultParameterValue(CASCADE_COUNTRY, defaultValue);
        ph.setMandatory(CASCADE_COUNTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataDontDetectNullSubstitutionLabel() throws CascadeResourceNotFoundException, InputControlValidationException {

        String[] rawData = new String[] {InputControlHandler.NULL_SUBSTITUTION_LABEL, "Mexico"};
        Object defaultValue = null;
        Object expectedValue = list(InputControlHandler.NULL_SUBSTITUTION_LABEL, "Mexico");

        ph.setDefaultParameterValue(CASCADE_COUNTRY, defaultValue);
        ph.setMandatory(CASCADE_COUNTRY, true);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataDoubleWithNestedTypeCommaSeparator() throws CascadeResourceNotFoundException, InputControlValidationException {

        ph.addParameterAndControlInfo(NUMBER, "Number", InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list(10.1, 11.2), true);
        ph.setParameterType(NUMBER, Collection.class, java.lang.Double.class);

        String[] rawData = new String[] {"12,3", "15,9999"};
        Object expectedValue = list(12.3, 15.9999);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataDoubleWithNestedTypeNull() throws CascadeResourceNotFoundException, InputControlValidationException {

        ph.addParameterAndControlInfo(NUMBER, "Number", InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list(10.1, 11.2), true);
        ph.setParameterType(NUMBER, Collection.class, java.lang.Double.class);

        String[] rawData = new String[] {""};
        Object expectedValue = nullList();

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataDoubleWithNestedTypeCommaSeparatorAndNull() throws CascadeResourceNotFoundException, InputControlValidationException {

        ph.addParameterAndControlInfo(NUMBER, "Number", InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list(10.1, 11.2), true);
        ph.setParameterType(NUMBER, Collection.class, java.lang.Double.class);

        String[] rawData = new String[] {"", "12,3", "15,9999"};
        Object expectedValue = list(null, 12.3, 15.9999);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataDoubleWithNestedTypeDotSeparator() throws CascadeResourceNotFoundException, InputControlValidationException {

        ph.addParameterAndControlInfo(NUMBER, "Number", InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list(10.1, 11.2), true);
        ph.setParameterType(NUMBER, Collection.class, java.lang.Double.class);

        String[] rawData = new String[] {"12.3", "15.9999"};
        Object expectedValue = list(12.3, 15.9999);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataDoubleParameterWasntSent() throws CascadeResourceNotFoundException, InputControlValidationException {

        ph.addParameterAndControlInfo(NUMBER, "Number", InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list(10.1, 11.2), true);
        ph.setParameterType(NUMBER, Collection.class, java.lang.Double.class);

        String[] rawData = null;
        Object expectedValue = list(10.1, 11.2);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataDoubleParameterWithoutNestedType() throws CascadeResourceNotFoundException, InputControlValidationException {

        ph.addParameterAndControlInfo(NUMBER, "Number", InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list(10.1, 11.2), true);

        String[] rawData = new String[] {"", "12,3", "15,9999"};
        Object expectedValue = list("", "12,3", "15,9999");

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(NUMBER),
                ph.getInputControlInfo().getInputControlInformation(NUMBER));

        assertEquals(expectedValue, convertedValue);
    }

    @Test
    public void convertParameterValueFromRawDataDateParameterWithNull() throws CascadeResourceNotFoundException, InputControlValidationException {

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(1997, GregorianCalendar.AUGUST, 20, 0, 0, 0);

        ph.addParameterAndControlInfo(DATE, "Date", InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null,
                gregorianCalendar.getTime(), true);
        ph.setParameterType(DATE, Collection.class, java.util.Date.class);

        String[] rawData = new String[] {"", "1997-03-15", "2012-11-05"};

        gregorianCalendar.setTimeInMillis(858409200000L); //gregorianCalendar.set(1997, GregorianCalendar.MARCH, 15);
        Date date1 = gregorianCalendar.getTime();
        gregorianCalendar.setTimeInMillis(1352098800000L); //gregorianCalendar.set(2012, GregorianCalendar.NOVEMBER, 5);
        Date date2 = gregorianCalendar.getTime();

        Object expectedValue = list(null, date1, date2);

        Object convertedValue = getHandler().convertParameterValueFromRawData(rawData, ph.getInputControl(DATE),
                ph.getInputControlInfo().getInputControlInformation(DATE));

        assertEquals(expectedValue, convertedValue);
    }

    /**
     * Select none                                  | has one default, default is in query results     | mandatory     = select default
     */
    @Test
    public void getValue1() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(null, null, null, null, null);
        ph.setDefaultParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, false), envelope);
    }

    /**
     * Select null (control value wasn't sent)            | has one default, default is in query results     | not mandatory = select default
     */
    @Test
    public void getValue2() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(null, null, null, null, null);
        ph.setDefaultParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, false), envelope);
    }

    /**
     * Select null (control value wasn't sent)      | has one default, default is not in query results | mandatory     = select first from query results
     */
    @Test
    public void getValue3() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(null, null, null, null, null);
        ph.setDefaultParameterValues(list("Germany"), list("Bavaria"), "Distribution", "Engineering", "D & A Carmona Machinery Group");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(true, false, false), envelope);
    }

    /**
     * Select null (control value wasn't sent)      | has one default, default is not in query results | not mandatory = select none
     */
    @Test
    public void getValue4() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(null, null, null, null, null);
        ph.setDefaultParameterValues(list("Germany"), list("Bavaria"), "Distribution", "Engineering", "D & A Carmona Machinery Group");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, false), envelope);
    }

    /**
     * Select null (control value wasn't sent)       | no default                                       | mandatory     = select first from query results
     */
    @Test
    public void getValue5() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(null, null, null, null, null);
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(true, false, false), envelope);
    }

    /**
     * Select null (control value wasn't sent)      | no default                                       | not mandatory = select none
     */
    @Test
    public void getValue6() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(null, null, null, null, null);
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, false), envelope);
    }

    /**
     * Select one, selected is in query results     | has one default, default is in query results     | mandatory     = select one which came to the method
     */
    @Test
    public void getValue7() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Canada"), list("BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, false), envelope);
    }

    /**
     * Select one, selected is in query results     | has one default, default is in query results     | not mandatory = select one which came to the method
     */
    @Test
    public void getValue8() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Canada"), list("BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, false), envelope);
    }

    /**
     * Select one, selected is in query results     | has one default, default is not in query results | mandatory     = select one which came to the method
     */
    @Test
    public void getValue9() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Germany"), list("Bavaria"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, false), envelope);
    }

    /**
     * Select one, selected is in query results     | has one default, default is not in query results | not mandatory = select one which came to the method
     */
    @Test
    public void getValue10() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Germany"), list("Bavaria"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, false), envelope);
    }

    /**
     * Select one, selected is in query results     | has no default                                   | mandatory     = select one which came to the method
     */
    @Test
    public void getValue11() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, false), envelope);
    }

    /**
     * Select one, selected is in query results     | has no default                                   | mandatory     = select one which came to the method
     */
    @Test
    public void getValue12() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, false), envelope);
    }

    /**
     * Select one, selected is in query results     | has one default, default is in query results     | mandatory     = select one which came to the method
     */
    @Test
    public void getValue123() throws Exception {
        mockExecuteQueryBean();
        ph.setArgumentCascadeParameterValues(nullList(), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada", NULL_SUBSTITUTION_VALUE + "|" + NULL_SUBSTITUTION_LABEL),
                list(false, false, false, true), envelope);
    }

    /**
     * Select one, selected is in query results     | has one default, default is in query results     | not mandatory = select one which came to the method
     */
    @Test
    public void getValue124() throws Exception {
        mockExecuteQueryBean();
        ph.setArgumentCascadeParameterValues(nullList(), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada", NULL_SUBSTITUTION_VALUE + "|" + NULL_SUBSTITUTION_LABEL),
                list(false, false, false, true), envelope);
    }

    /**
     * Select one, selected is not in query results | has one default, default is in query results     | mandatory     = select default
     */
    @Test
    public void getValue13() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Germany"), list("Bavaria"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Canada"), list("BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, true), envelope);
    }

    /**
     * Select one, selected is not in query results | has one default, default is in query results     | not mandatory = select default
     */
    @Test
    public void getValue14() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Germany"), list("Bavaria"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Canada"), list("BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, true), envelope);
    }

    /**
     * Select one, selected is not in query results | has one default, default is not in query results | mandatory     = select first from query results
     */
    @Test
    public void getValue15() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Germany"), list("Bavaria"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("France"), list("Alsace"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(true, false, false), envelope);
    }

    /**
     * Select one, selected is not in query results | has one default, default is not in query results | not mandatory = select none
     */
    @Test
    public void getValue16() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Germany"), list("Bavaria"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("France"), list("Alsace"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, false), envelope);
    }

    /**
     * Select one, selected is not in query results | no default                                       | mandatory     = select first from query results
     */
    @Test
    public void getValue17() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Germany"), list("Bavaria"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(true, false, false), envelope);
    }

    /**
     * Select one, selected is not in query results | no default                                       | not mandatory = select none
     */
    @Test
    public void getValue18() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Germany"), list("Bavaria"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, false), envelope);
    }

    /**
     * Select two, both are in query results        | has two defaults, both are in query results      | mandatory     = select two which came to the method
     */
    @Test
    public void getValue19() throws CascadeResourceNotFoundException  {
        ph.setArgumentCascadeParameterValues(list("Ukraine", "Canada"), list("Zakarpatska", "BC"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("USA", "Canada"), list("CA", "BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, true), envelope);
    }

    /**
     * Select two, both are in query results        | has two defaults, both are in query results      | not mandatory = select two which came to the method
     */
    @Test
    public void getValue20() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("USA", "Canada"), list("Zakarpatska", "BC"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Ukraine", "Canada"), list("CA", "BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(true, false, true), envelope);
    }

    /**
     * Select two, both are in query results        | no defaults                                      | mandatory     = select two which came to the method
     */
    @Test
    public void getValue21() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Ukraine", "Canada"), list("Zakarpatska", "BC"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, true), envelope);
    }

    /**
     * Select two, both are in query results        | no defaults                                      | not mandatory = select two which came to the method
     */
    @Test
    public void getValue22() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Ukraine", "Canada"), list("Zakarpatska", "BC"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, true), envelope);
    }

    /**
     * Select two, one is in query results          | has two defaults, both are in query results      | mandatory     = select one which came to the method and is in query results
     */
    @Test
    public void getValue23() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Ukraine", "Germany"), list("Zakarpatska", "Bavaria"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("USA", "Canada"), list("CA", "BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, false), envelope);
    }

    /**
     * Select two, one is in query results          | no defaults                                      | mandatory     = select one which came to the method and is in query results
     */
    @Test
    public void getValue24() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Ukraine", "Germany"), list("Zakarpatska", "Bavaria"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, false), envelope);
    }

    /**
     * Select two, one is in query results          | no defaults                                      | not mandatory = select one which came to the method and is in query results
     */
    @Test
    public void getValue25() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Ukraine", "Germany"), list("Zakarpatska", "Bavaria"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, true, false), envelope);
    }

    /**
     * Select two, no one is in query results       | has two defaults, both are in query results      | mandatory     = select two defaults
     */
    @Test
    public void getValue26() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Germany", "France"), list("Bavaria", "Alsace"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("USA", "Canada"), list("CA", "BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(true, false, true), envelope);
    }

    /**
     * Select two, no one is in query results       | has two defaults, both are in query results      | not mandatory = select two defaults
     */
    @Test
    public void getValue27() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Germany", "France"), list("Bavaria", "Alsace"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("USA", "Canada"), list("CA", "BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(true, false, true), envelope);
    }

    /**
     * Select two, no one is in query results       | has two defaults, one is in query results        | mandatory     = select one default which is in query results
     */
    @Test
    public void getValue28() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Germany", "France"), list("Bavaria", "Alsace"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Italy", "Canada"), list("Sicily", "BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, true), envelope);
    }

    /**
     * Select two, no one is in query results       | has two defaults, one is in query results        | not mandatory = select one default which is in query results
     */
    @Test
    public void getValue29() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Germany", "France"), list("Bavaria", "Alsace"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Italy", "Canada"), list("Sicily", "BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, true), envelope);
    }

    /**
     * Select two, no one is in query results       | has two defaults, no one is in query results     | mandatory     = select first from query results
     */
    @Test
    public void getValue30() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Germany", "France"), list("Bavaria", "Alsace"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Italy", "Romania"), list("Sicily", "Northeast"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(true, false, false), envelope);
    }

    /**
     * Select two, no one is in query results       | has two defaults, no one is in query results     | not mandatory = select none
     */
    @Test
    public void getValue31() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Germany", "France"), list("Bavaria", "Alsace"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Italy", "Romania"), list("Sicily", "Northeast"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, false), envelope);
    }

    /**
     * Select two, no one is in query results       | no defaults                                      | mandatory     = select first from query results
     */
    @Test
    public void getValue32() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Germany", "France"), list("Bavaria", "Alsace"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(true, false, false), envelope);
    }

    /**
     * Select two, no one is in query results       | no defaults                                      | not mandatory = select none
     */
    @Test
    public void getValue33() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("Germany", "France"), list("Bavaria", "Alsace"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(null, null, null, null, null);
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, false), envelope);
    }

    /**
     * Empty selection for master control            | default is in query results                      | mandatory     = nothing selected
     */
    @Test
    public void getValue34() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list(), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Canada"), list("BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, false), envelope);
    }

    /**
     * Empty selection for master control            | default is in query results                      | not mandatory = select none
     */
    @Test
    public void getValue35() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list(), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Canada"), list("BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, false), envelope);
    }

    /**
     * Empty selection for master control            | default is not in query results                  | mandatory     = nothing selected
     */
    @Test
    public void getValue36() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list(), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Germany"), list("BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, false), envelope);
    }

    /**
     * Empty selection for master control            | default is not in query results                  | not mandatory = select none
     */
    @Test
    public void getValue37() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list(), list("Zakarpatska"), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Germany"), list("BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_COUNTRY), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("USA", "Ukraine", "Canada"), list(false, false, false), envelope);
    }

    /**
     * Empty selection for slave control             | default is in query results                      | mandatory     = nothing selected
     */
    @Test
    public void getValue38() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("USA"), list(), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("USA"), list("CA", "WA"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);
        ph.setMandatory(CASCADE_STATE, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_STATE), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_STATE));

        assertMultiSelectEnvelope(list("CA", "OR", "WA"), list(false, false, false), envelope);
    }

    /**
     * Empty selection for slave control             | default is in query results                      | not mandatory = select none
     */
    @Test
    public void getValue39() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("USA"), list(), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("USA"), list("CA", "WA"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);
        ph.setMandatory(CASCADE_STATE, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_STATE), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_STATE));

        assertMultiSelectEnvelope(list("CA", "OR", "WA"), list(false, false, false), envelope);
    }

    /**
     * Empty selection for slave control             | default is not in query results                  | mandatory     = nothing selected
     */
    @Test
    public void getValue40() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("USA"), list(), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Canada"), list("BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);
        ph.setMandatory(CASCADE_STATE, true);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_STATE), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("CA", "OR", "WA"), list(false, false, false), envelope);
    }

    /**
     * Empty selection for slave control             | default is not in query results                  | not mandatory = select none
     */
    @Test
    public void getValue41() throws CascadeResourceNotFoundException {
        ph.setArgumentCascadeParameterValues(list("USA"), list(), "Manufactoring", "Telecommunications", "Fulcher-Berg Engineering Corp");
        ph.setDefaultParameterValues(list("Canada"), list("BC"), "Distribution", "Engineering", "Y & B Bomar Communications Partners");
        ph.setMandatory(CASCADE_COUNTRY, true);
        ph.setMandatory(CASCADE_STATE, false);

        InputControlState envelope = getHandler().getState(ph.getInputControl(CASCADE_STATE), dataSource.getMock(),
                ph.getArgumentCascadeParameterValues(), ph.getParameterTypes(), ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertMultiSelectEnvelope(list("CA", "OR", "WA"), list(false, false, false), envelope);
    }

    /**
     * Test cases:
     *
     * Null means that parameter was not passed at all and there is no default
     *
     * 1 Null                                           | has one default, default is in query results     | mandatory     = select default
     * 2 Null                                           | has one default, default is in query results     | not mandatory = select default
     * 3 Null                                           | has one default, default is not in query results | mandatory     = select first from query results
     * 4 Null                                           | has one default, default is not in query results | not mandatory = select none
     * 5 Null                                           | no default                                       | mandatory     = select first from query results
     * 6 Null                                           | no default                                       | not mandatory = select none
     *
     * 7 Select one, selected is in query results       | has one default, default is in query results     | mandatory     = select one which came to the method
     * 8 Select one, selected is in query results       | has one default, default is in query results     | not mandatory = select one which came to the method
     * 9 Select one, selected is in query results       | has one default, default is not in query results | mandatory     = select one which came to the method
     * 10 Select one, selected is in query results      | has one default, default is not in query results | not mandatory = select one which came to the method
     * 11 Select one, selected is in query results      | has no default                                   | mandatory     = select one which came to the method
     * 12 Select one, selected is in query results      | has no default                                   | not mandatory = select one which came to the method
     * 123 Select one, selected is in query results     | has one default, default is in query results     | mandatory     = select one which came to the method
     * 124 Select one, selected is in query results     | has one default, default is in query results     | not mandatory = select one which came to the method
     *
     * 13 Select one, selected is not in query results  | has one default, default is in query results     | mandatory     = select default
     * 14 Select one, selected is not in query results  | has one default, default is in query results     | not mandatory = select default
     * 15 Select one, selected is not in query results  | has one default, default is not in query results | mandatory     = select first from query results
     * 16 Select one, selected is not in query results  | has one default, default is not in query results | not mandatory = select none
     * 17 Select one, selected is not in query results  | no default                                       | mandatory     = select first from query results
     * 18 Select one, selected is not in query results  | no default                                       | not mandatory = select none
     *
     * 19 Select two, both are in query results         | has two defaults, both are in query results      | mandatory     = select two which came to the method
     * 20 Select two, both are in query results         | has two defaults, both are in query results      | not mandatory = select two which came to the method
     * 21 Select two, both are in query results         | no defaults                                      | mandatory     = select two which came to the method
     * 22 Select two, both are in query results         | no defaults                                      | not mandatory = select two which came to the method
     * 23 Select two, one is in query results           | has two defaults, both are in query results      | mandatory     = select one which came to the method and is in query results
     * 24 Select two, one is in query results           | no defaults                                      | mandatory     = select one which came to the method and is in query results
     * 25 Select two, one is in query results           | no defaults                                      | not mandatory = select one which came to the method and is in query results
     * 26 Select two, no one is in query results        | has two defaults, both are in query results      | mandatory     = select two defaults
     * 27 Select two, no one is in query results        | has two defaults, both are in query results      | not mandatory = select two defaults
     * 28 Select two, no one is in query results        | has two defaults, one is in query results        | mandatory     = select one default which is in query results
     * 29 Select two, no one is in query results        | has two defaults, one is in query results        | not mandatory = select one default which is in query results
     * 30 Select two, no one is in query results        | has two defaults, no one is in query results     | mandatory     = select first from query results
     * 31 Select two, no one is in query results        | has two defaults, no one is in query results     | not mandatory = select none
     * 32 Select two, no one is in query results        | no defaults                                      | mandatory     = select first from query results
     * 33 Select two, no one is in query results        | no defaults                                      | not mandatory = select none
     *
     * Empty list for getState means that ~NOTHING~ was submitted from client
     *
     * 34 Empty selection for master control            | default is in query results                      | mandatory     = select first
  =  * 35 Empty selection for master control            | default is in query results                      | not mandatory = select none
     * 36 Empty selection for master control            | default is not in query results                  | mandatory     = select first
     * 37 Empty selection for master control            | default is not in query results                  | not mandatory = select none
     * 38 Empty selection for slave control             | default is in query results                      | mandatory     = select first
  =  * 39 Empty selection for slave control             | default is in query results                      | not mandatory = select none
     * 40 Empty selection for slave control             | default is not in query results                  | mandatory     = select first
     * 41 Empty selection for slave control             | default is not in query results                  | not mandatory = select none
     *
     * NOTE:
     * Parameters like:
     *
     *   <parameter name="Country_multi_select" class="java.util.Collection">
     *       <defaultValueExpression><![CDATA[null]]></defaultValueExpression>
     *   </parameter>
     *   <parameter name="Cascading_state_multi_select" class="java.util.Collection">
     *       <defaultValueExpression><![CDATA[new ArrayList(Arrays.asList(new String[] {"CA","WA"}))]]></defaultValueExpression>
     *   </parameter>
     *   <parameter name="Cascading_name_single_select" class="java.lang.String"/>
     *
     * Will have the following default values:
     * {Country_multi_select=null, Cascading_name_single_select=null, Cascading_state_multi_select=[CA, WA]}
     *
     * It means that now we can't make difference whether the parameter hasn't default at all, or it's intended null.
     *
     */

    @Test
    public void formatValueString() throws CascadeResourceNotFoundException {

        List<String> typedValue = list("USA", "Ukraine", "Canada");

        ph.setParameterType(CASCADE_COUNTRY, List.class, null);
        ph.setDataType(CASCADE_COUNTRY, (Byte) null);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));


        assertReflectionEquals(array("USA", "Ukraine", "Canada"), formattedValues);
    }

    @Test
    public void formatValueDouble() throws CascadeResourceNotFoundException {

        List<Double> typedValue = list(10.1, 10.2, 10.1234567);

        ph.setParameterType(CASCADE_COUNTRY, List.class, null);
        ph.setDataType(CASCADE_COUNTRY, (Byte) null);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(LocaleContextHolder.getLocale());
        String separator = Character.toString(df.getDecimalFormatSymbols().getDecimalSeparator());
        assertReflectionEquals(array("10" + separator + "1", "10" + separator + "2", "10" + separator + "1234567"), formattedValues);
    }

    @Test
    public void formatEmptyList() throws CascadeResourceNotFoundException {

        List typedValue = list();

        ph.setParameterType(CASCADE_COUNTRY, List.class, null);
        ph.setDataType(CASCADE_COUNTRY, (Byte) null);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertReflectionEquals(array(), formattedValues);
    }

    @Test
    public void formatListWithNull() throws CascadeResourceNotFoundException {

        List typedValue = nullList();

        ph.setParameterType(CASCADE_COUNTRY, List.class, null);
        ph.setDataType(CASCADE_COUNTRY, (Byte) null);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertReflectionEquals(array(InputControlHandler.NULL_SUBSTITUTION_VALUE), formattedValues);
    }

    @Test
    public void formatListNull() throws CascadeResourceNotFoundException {

        List typedValue = null;

        ph.setParameterType(CASCADE_COUNTRY, List.class, null);
        ph.setDataType(CASCADE_COUNTRY, (Byte) null);

        String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(CASCADE_COUNTRY),
                ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));

        assertReflectionEquals(array(), formattedValues);
    }

    @Test
    public void formatListNonCollection() throws CascadeResourceNotFoundException {

        Object typedValue = "test";

        ph.setParameterType(CASCADE_COUNTRY, List.class, null);
        ph.setDataType(CASCADE_COUNTRY, (Byte) null);

        String exceptionMessage = null;
        try {
            String[] formattedValues = getHandler().formatValue(typedValue, ph.getInputControl(CASCADE_COUNTRY),
                    ph.getInputControlInfo().getInputControlInformation(CASCADE_COUNTRY));
        } catch (IllegalArgumentException e) {
            exceptionMessage = e.getMessage();
        }

        assertReflectionEquals("Value for MultiSelect control cannot be java.lang.String, it should be a Collection.", exceptionMessage);
    }

    @Before
    public void before() throws CascadeResourceNotFoundException {
        createParameters();
        mockExecuteQuery();
    }

    /**
     * Create Parameters helper object
     */
    private void createParameters() {
        if (ph == null) {
            ph = new ParametersHelper();
            ph.addParameterAndControlInfo(CASCADE_COUNTRY, "Billing Country", InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("USA"), true, "/country", "country");
            ph.setInputControlQuery(CASCADE_COUNTRY, "");

            ph.addParameterAndControlInfo(CASCADE_STATE, "Billing State", InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("CA"), true, "/state", "state");
            ph.setInputControlQuery(CASCADE_STATE, CASCADE_COUNTRY);

            ph.addParameterAndControlInfo(CASCADE_ACCOUNT_TYPE, "Account Type", InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Consulting", true, "/accountType", "accountType");
            ph.setInputControlQuery(CASCADE_ACCOUNT_TYPE, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).toString());

            ph.addParameterAndControlInfo(CASCADE_INDUSTRY, "Industry", InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Engineering", true, "/industry", "industry");
            ph.setInputControlQuery(CASCADE_INDUSTRY, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).toString());

            ph.addParameterAndControlInfo(CASCADE_NAME, "Company Name", InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "EngBureau, Ltd", true, "/name", "name");
            ph.setInputControlQuery(CASCADE_NAME, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).append(",").append(CASCADE_INDUSTRY).toString());
        }
    }

    private InputControlHandler getHandler() throws CascadeResourceNotFoundException {
        Map<String, Object> mockedServices = new LinkedHashMap<String, Object>();
        Mock<CachedRepositoryService> cachedRepositoryService = MockUnitils.createMock(CachedRepositoryService.class);
        setUpCachedRepositoryService(cachedRepositoryService);
        mockedServices.put("cachedRepositoryService", cachedRepositoryService.getMock());
        mockedServices.put("filterResolver", createFilterResolver());
        mockedServices.put("cachedEngineService", cachedEngineService.getMock());
        mockedServices.put("engineService", createEngineService());
        mockedServices.put("isoCalendarFormatProvider", createCalendarFormatProvider());
        mockedServices.put("messageSource", createMessageSource());

        ApplicationContext context = setUpApplicationContext(mockedServices, "classpath:/com/jaspersoft/jasperserver/war/cascade/applicationContext-cascade-test.xml");

        Map<String, Map<String, Object>> config = (Map<String, Map<String, Object>>)context.getBean("inputControlTypeNewConfiguration");
        return (InputControlHandler)config.get("7").get("handler");
    }

    private void mockExecuteQuery() throws CascadeResourceNotFoundException {
        cachedEngineService.resetBehavior();
        cachedEngineService.performs(new MockBehavior() {
            public OrderedMap execute(ProxyInvocation proxyInvocation) throws Throwable {
                List<Object> args = proxyInvocation.getArguments();
                String controlName = (String) args.get(7);
                return EngineServiceCascadeTestQueryExecutor.executeOrderedMap(controlName, (Map<String, Object>) args.get(5));
            }
        }).executeQuery(null, null, null, null, null, null, null, null);
    }

    private void mockExecuteQueryBean() throws Exception {
        ApplicationContext context = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/multiSelectQueryInputControlHandler-fixtures-getValue123.xml");
        cachedEngineService.resetBehavior();
        CascadeTestHelper.setUpCachedEngineService(cachedEngineService, context, "getValue123");
    }

    private void setUpCachedRepositoryService(final Mock<CachedRepositoryService> cachedRepositoryServiceMock) throws CascadeResourceNotFoundException {
        cachedRepositoryServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                List<Object> args = proxyInvocation.getArguments();
                if (Query.class.isAssignableFrom((Class) args.get(0))) {
                    return ((ResourceReference) args.get(1)).getLocalResource();
                } else {
                    return null;
                }
            }
        }).getResource(null, (ResourceReference) null);
    }

    private void assertMultiSelectEnvelope(List<String> expectedOptionNames, List<Boolean> expectedSelectedOptions, InputControlState actualEnvelope) {
        if (expectedOptionNames.size() != expectedSelectedOptions.size()) fail("Size of expected names list and expected selection list do ot match!");

        assertNull(actualEnvelope.getValue());

        assertEquals(expectedOptionNames.size(), actualEnvelope.getOptions().size());

        for (int i = 0; i < expectedOptionNames.size(); i++) {
            String[] valueAndLabel = expectedOptionNames.get(i).split("\\|");
            assertEquals(valueAndLabel[0], actualEnvelope.getOptions().get(i).getValue());
            assertEquals(valueAndLabel.length > 1 ? valueAndLabel[1] : valueAndLabel[0], actualEnvelope.getOptions().get(i).getLabel());
            assertEquals(expectedSelectedOptions.get(i), (Boolean) actualEnvelope.getOptions().get(i).isSelected());
        }
    }
}
