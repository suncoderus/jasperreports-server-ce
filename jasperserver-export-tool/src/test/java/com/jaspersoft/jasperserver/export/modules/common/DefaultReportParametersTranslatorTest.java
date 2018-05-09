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
package com.jaspersoft.jasperserver.export.modules.common;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import net.sf.jasperreports.types.date.DateRangeBuilder;
import net.sf.jasperreports.types.date.DateRangeExpression;
import net.sf.jasperreports.types.date.FixedTimestamp;
import net.sf.jasperreports.types.date.RelativeDateRange;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportLoadingService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControlsContainer;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.export.modules.common.rd.DateRangeDTO;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.types.date.DateRange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultReportParametersTranslatorTest {
    @InjectMocks
    private DefaultReportParametersTranslator translator = new DefaultReportParametersTranslator();

    @Mock
    private ReportLoadingService reportLoadingService;

    @Test
    public void ensureRelativeDateDTOUsedForDateRangeObjects() {
        final String paramName = "dateP";
        final String dateRangeExpression = "WEEK";
        final Map<String, ?> map = map(entry(paramName, new DateRangeBuilder(dateRangeExpression).toDateRange()));

        //Actual test method call
        ReportParameterValueBean[] beanValues = translator.getBeanParameterValues("/uri", map);

        //assert expectations
        assertEquals(1, beanValues.length);
        assertEquals(paramName, beanValues[0].getName());
        assertEquals(1, beanValues[0].getValues().length);
        final DateRangeDTO dateRangeDTO = (DateRangeDTO) (beanValues[0].getValues()[0]);
        assertDateRangeDTOEquals(dto(dateRangeExpression, null, Date.class.getName()), dateRangeDTO);
    }

    @Test
    public void ensureRelativeDateDTOWithDateUsedForDateRangeObjects() {
        final String paramName = "dateP";
        final Date date = new GregorianCalendar(2012, Calendar.OCTOBER, 1, 0, 0, 0).getTime();
        final Map<String, ?> map = map(entry(paramName, new DateRangeBuilder(date).toDateRange()));

        //Actual test method call
        ReportParameterValueBean[] beanValues = translator.getBeanParameterValues("/uri", map);

        //assert expectations
        assertEquals(1, beanValues.length);
        assertEquals(paramName, beanValues[0].getName());
        assertEquals(1, beanValues[0].getValues().length);
        assertDateRangeDTOEquals(dto(null, date, Date.class.getName()), (DateRangeDTO) (beanValues[0].getValues()[0]));
    }

    @Test
    public void ensureRelativeDateDTOWithTimestampUsedForDateRangeObjects() {
        final String paramName = "dateP";
        final Date date = new GregorianCalendar(2012, Calendar.OCTOBER, 1, 0, 0, 0).getTime();
        final Map<String, ?> map = map(entry(paramName, new DateRangeBuilder(date).set(Timestamp.class).toDateRange()));

        //Actual test method call
        ReportParameterValueBean[] beanValues = translator.getBeanParameterValues("/uri", map);

        //assert expectations
        assertEquals(1, beanValues.length);
        assertEquals(paramName, beanValues[0].getName());
        assertEquals(1, beanValues[0].getValues().length);
        assertDateRangeDTOEquals(dto(null, date, Timestamp.class.getName()), (DateRangeDTO) (beanValues[0].getValues()[0]));
    }

    @Test
    public void ensureRelativeDateDTOWithListUsedForDateRangeObjects() {
        final String paramName = "dateP";
        final Date date = new GregorianCalendar(2012, Calendar.OCTOBER, 1, 0, 0, 0).getTime();

        final DateRange dr1 = new DateRangeBuilder(date).set(Timestamp.class).toDateRange();
        final DateRange dr2 = new DateRangeBuilder(date).set(Date.class).toDateRange();

        final Map<String, ?> map = map(entry(paramName, list(dr1, dr2)));

        //Actual test method call
        ReportParameterValueBean[] beanValues = translator.getBeanParameterValues("/uri", map);

        //assert expectations
        assertEquals(1, beanValues.length);
        assertEquals(paramName, beanValues[0].getName());
        assertEquals(2, beanValues[0].getValues().length);
        assertDateRangeDTOEquals(dto(null, date, Timestamp.class.getName()), (DateRangeDTO) (beanValues[0].getValues()[0]));
        assertDateRangeDTOEquals(dto(null, date, Date.class.getName()), (DateRangeDTO) (beanValues[0].getValues()[1]));
    }

    @Test
    public void ensureRelativeDateDTOWithNullsInListUsedForDateRangeObjects() {
        final String paramName = "dateP";
        final Date date = new GregorianCalendar(2012, Calendar.OCTOBER, 1, 0, 0, 0).getTime();

        final DateRange dr2 = new DateRangeBuilder(date).set(Date.class).toDateRange();

        final Map<String, ?> map = map(entry(paramName, list(null, dr2)));

        //Actual test method call
        ReportParameterValueBean[] beanValues = translator.getBeanParameterValues("/uri", map);

        //assert expectations
        assertEquals(1, beanValues.length);
        assertEquals(paramName, beanValues[0].getName());
        assertEquals(2, beanValues[0].getValues().length);
        assertEquals(DefaultReportParametersTranslator.NULL_SUBSTITUTE, beanValues[0].getValues()[0]);
        assertDateRangeDTOEquals(dto(null, date, Date.class.getName()), (DateRangeDTO) (beanValues[0].getValues()[1]));
    }

    @Test
    public void ensureRelativeDateDTOWithNullsInArrayUsedForDateRangeObjects() {
        final String paramName = "dateP";
        final Date date = new GregorianCalendar(2012, Calendar.OCTOBER, 1, 0, 0, 0).getTime();

        final DateRange dr2 = new DateRangeBuilder(date).set(Date.class).toDateRange();

        final Map<String, ?> map = map(entry(paramName, array(null, dr2)));

        //Actual test method call
        ReportParameterValueBean[] beanValues = translator.getBeanParameterValues("/uri", map);

        //assert expectations
        assertEquals(1, beanValues.length);
        assertEquals(paramName, beanValues[0].getName());
        assertEquals(2, beanValues[0].getValues().length);
        assertEquals(DefaultReportParametersTranslator.NULL_SUBSTITUTE, beanValues[0].getValues()[0]);
        assertDateRangeDTOEquals(dto(null, date, Date.class.getName()), (DateRangeDTO) (beanValues[0].getValues()[1]));
    }

    @Test
    public void ensureStringParamValuesHandledCorrectly() {
        final String paramName = "paramName";
        final String paramValue = "value";

        final Map<String, ?> map = map(entry(paramName, paramValue));

        //Actual test method call
        ReportParameterValueBean[] beanValues = translator.getBeanParameterValues("/uri", map);

        //assert expectations
        assertEquals(1, beanValues.length);
        assertEquals(paramName, beanValues[0].getName());
        assertEquals(1, beanValues[0].getValues().length);
        assertEquals(paramValue, beanValues[0].getValues()[0]);
    }

    @Test
    public void ensureStringCollectionParamValuesHandledCorrectly() {
        final String paramName = "paramName";
        final String paramValue = "value";

        final Map<String, ?> map = map(entry(paramName, list(null, paramValue)));

        //Actual test method call
        ReportParameterValueBean[] beanValues = translator.getBeanParameterValues("/uri", map);

        //assert expectations
        assertEquals(1, beanValues.length);
        assertEquals(paramName, beanValues[0].getName());
        assertEquals(2, beanValues[0].getValues().length);
        assertEquals(DefaultReportParametersTranslator.NULL_SUBSTITUTE, beanValues[0].getValues()[0]);
        assertEquals(paramValue, beanValues[0].getValues()[1]);
    }

    @Test
    public void ensureNullParameterValueHandledAsNullSubstitution() {
        final String paramName = "param";
        ReportParameterValueBean[] beanValues = translator.getBeanParameterValues("/uri", map(entry(paramName, null)));

        assertEquals(1, beanValues.length);
        assertEquals(paramName, beanValues[0].getName());
        assertEquals(DefaultReportParametersTranslator.NULL_SUBSTITUTE, beanValues[0].getValues()[0]);
    }


    @Test
    public void ensureNullIsReturnedForNullOrEmptyValuesMap() {
        //Actual test method call
        ReportParameterValueBean[] beanValuesNull = translator.getBeanParameterValues("/uri", null);
        ReportParameterValueBean[] beanValuesEmpty = translator.getBeanParameterValues("/uri", map());

        //assert expectations
        assertNull(beanValuesNull);
        assertNull(beanValuesEmpty);
    }

    @Test
    public void ensureCorrectDateRangeCreatedFromDateRangeDTO() {
        reset(reportLoadingService);

        final String control1Name = "control1";
        final InputControl control = control(control1Name, InputControl.TYPE_SINGLE_VALUE);
        when(reportLoadingService.getInputControls(any(ExecutionContext.class), any(InputControlsContainer.class)))
                .thenReturn(list(control));

        //Need to mock getReport method since it is very hard to go through real implementation
        DefaultReportParametersTranslator spy = spy(translator);
        doReturn(jrReport()).when(spy).getReport(any(ReportUnit.class));

        //Actual test method call
        Map<String, Object> paramValues = spy.getParameterValues(
                reportUnit(), array(valueBean(control1Name, dto("WEEK", null, Date.class.getName()))), null);

        verify(reportLoadingService, times(1)).getInputControls(any(ExecutionContext.class), any(InputControlsContainer.class));
        assertEquals(1, paramValues.size());

        final Map.Entry<String, Object> entry = paramValues.entrySet().iterator().next();
        assertEquals(control1Name, entry.getKey());
        assertEquals(RelativeDateRange.class, entry.getValue().getClass());
        assertEquals("WEEK", ((DateRangeExpression)entry.getValue()).getExpression());
    }

    @Test
    public void ensureCorrectDateRangesCreatedFromDateRangeDTO() {
        reset(reportLoadingService);

        final String control1Name = "control1";
        final InputControl control1 = control(control1Name, InputControl.TYPE_MULTI_SELECT_QUERY);
        when(reportLoadingService.getInputControls(any(ExecutionContext.class), any(InputControlsContainer.class)))
                .thenReturn(list(control1));

        //Need to mock getReport method since it is very hard to go through real implementation
        DefaultReportParametersTranslator spy = spy(translator);
        doReturn(jrReport()).when(spy).getReport(any(ReportUnit.class));

        //Actual test method call
        Map<String, Object> paramValues = spy.getParameterValues(
                reportUnit(),
                array(valueBean(control1Name, dto("WEEK", null, Date.class.getName()), dto(null, new Date(2342343523452L), Timestamp.class.getName()))), null);

        verify(reportLoadingService, times(1)).getInputControls(any(ExecutionContext.class), any(InputControlsContainer.class));
        assertEquals(1, paramValues.size());

        final Iterator<Map.Entry<String,Object>> iterator = paramValues.entrySet().iterator();
        final Map.Entry<String, Object> entry1 = iterator.next();

        assertEquals(control1Name, entry1.getKey());

        Collection values = (Collection)entry1.getValue();

        final Iterator iterator1 = values.iterator();
        final Object value1 = iterator1.next();
        final Object value2 = iterator1.next();
        assertTrue(value1 instanceof RelativeDateRange);
        assertEquals("WEEK", ((DateRangeExpression)value1).getExpression());
        assertTrue(value2 instanceof FixedTimestamp);
        assertEquals(new Timestamp(2342343523452L), ((DateRange)value2).getStart());
    }

    @Test
    public void ensureNullsHandledCorrectly() {
        reset(reportLoadingService);

        reset(reportLoadingService);

        final String control1Name = "control1";
        final InputControl control = control(control1Name, InputControl.TYPE_SINGLE_VALUE);
        when(reportLoadingService.getInputControls(any(ExecutionContext.class), any(InputControlsContainer.class)))
                .thenReturn(list(control));

        //Need to mock getReport method since it is very hard to go through real implementation
        DefaultReportParametersTranslator spy = spy(translator);
        doReturn(jrReport()).when(spy).getReport(any(ReportUnit.class));

        //Actual test method call
        Map<String, Object> paramValues1 = spy.getParameterValues(reportUnit(), null, null);
        Map<String, Object> paramValues2 = spy.getParameterValues(reportUnit(), new ReportParameterValueBean[0], null);
        Map<String, Object> paramValues3 = spy.getParameterValues(reportUnit(), array(valueBean(control1Name, (Object) null)), null);
        Map<String, Object> paramValues4 = spy.getParameterValues(reportUnit(), array(new ReportParameterValueBean(control1Name, null)), null);
        Map<String, Object> paramValues5 = spy.getParameterValues(reportUnit(), array(valueBean(control1Name, DefaultReportParametersTranslator.NULL_SUBSTITUTE)), null);

        assertNull(paramValues1);
        assertNull(paramValues2);

        assertNotNull(paramValues3);
        assertNotNull(paramValues4);
        assertNotNull(paramValues5);

        assertNull(paramValues3.get(control1Name));
        assertNull(paramValues4.get(control1Name));
        assertNull(paramValues5.get(control1Name));
    }

    private static JRReport jrReport() {
        JRReport jrReport = mock(JRReport.class);
        return jrReport;
    }

    private static InputControl control(String name, byte type) {
        InputControl inputControl = mock(InputControl.class);
        when(inputControl.getName()).thenReturn(name);
        when(inputControl.getInputControlType()).thenReturn(type);

        return inputControl;
    }

    private static ReportParameterValueBean valueBean(Object name, Object... values) {
        ReportParameterValueBean valueBean = new ReportParameterValueBean();
        valueBean.setName(name);
        valueBean.setValues(values);

        return valueBean;
    }

    private static ReportUnit reportUnit() {
        ReportUnit reportUnitMock = mock(ReportUnit.class);

        return reportUnitMock;
    }

    private static void assertDateRangeDTOEquals(DateRangeDTO expected, DateRangeDTO actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertNotNull(expected);
            assertEquals(expected.getDate(), actual.getDate());
            assertEquals(expected.getExpression(), actual.getExpression());
            assertEquals(expected.getValueClass(), actual.getValueClass());
        }
    }


    private static DateRangeDTO dto(String expression, Date date, String valueClass) {
        DateRangeDTO dto = new DateRangeDTO();
        dto.setDate(date);
        dto.setExpression(expression);
        dto.setValueClass(valueClass);

        return dto;
    }

    private static Map<String, ?> map(Map.Entry<String, ?>... enties) {
        Map<String, Object> map = new HashMap<String, Object>(enties.length);
        for (Map.Entry<String, ?> entry: enties) {
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    private static Map.Entry<String, ?> entry(String key, Object value) {
        return new AbstractMap.SimpleEntry<String, Object>(key, value);
    }

    private static <T> List<T> list(T... objects) {
        List<T> list = new ArrayList<T>(objects.length);

        Collections.addAll(list, objects);

        return list;
    }

    private static <T> T[] array(T... objects) {
        return objects;
    }
}
