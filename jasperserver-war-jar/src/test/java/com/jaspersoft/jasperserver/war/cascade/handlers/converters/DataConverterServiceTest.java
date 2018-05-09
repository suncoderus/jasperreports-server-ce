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
package com.jaspersoft.jasperserver.war.cascade.handlers.converters;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JasperReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.war.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.DateRangeBuilder;
import net.sf.jasperreports.types.date.TimestampRange;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createCalendarFormatProvider;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.getInputControl;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.getInputControlInfo;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.injectDependencyToPrivateField;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.setUpApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link DataConverterServiceImpl}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class DataConverterServiceTest extends UnitilsJUnit4 {

    @TestedObject
    private DataConverterServiceImpl dataConverterService;

    @InjectInto(property = "cachedRepositoryService")
    private Mock<CachedRepositoryService> cachedRepositoryService;

    @InjectInto(property = "genericTypeProcessorRegistry")
    private Mock<GenericTypeProcessorRegistry> genericTypeProcessorRegistry;

    @InjectInto(property = "messageSource")
    private Mock<MessageSource> messageSource;

    @Test
    public void conversionOfParameterValueOfClassNumberIsSupportedAndConvertedToBigDecimal() throws Exception{
        genericTypeProcessorRegistry.returns(new NumberDataConverter()).getTypeProcessor(Number.class, DataConverter.class);
        JasperReportInputControlInformation reportInputControlInformation = new JasperReportInputControlInformation();
        JRDesignParameter parameter = new JRDesignParameter();
        parameter.setValueClass(Number.class);
        reportInputControlInformation.setReportParameter(parameter);
        final Object typedValue = dataConverterService.convertSingleValue("2", null, reportInputControlInformation);
        assertTrue(typedValue instanceof BigDecimal);
        assertTrue(BigDecimal.valueOf(2).equals(typedValue));
    }

    @Test
    public void conversionOfParameterValueOfNestedTypeNumberIsSupportedAndConvertedToBigDecimal() throws Exception{
        genericTypeProcessorRegistry.returns(new NumberDataConverter()).getTypeProcessor(Number.class, DataConverter.class);
        JasperReportInputControlInformation reportInputControlInformation = new JasperReportInputControlInformation();
        JRDesignParameter parameter = new JRDesignParameter();
        parameter.setValueClass(Collection.class);
        parameter.setNestedType(Number.class);
        reportInputControlInformation.setReportParameter(parameter);
        final Object typedValue = dataConverterService.convertSingleValue("2", null, reportInputControlInformation);
        assertTrue(typedValue instanceof BigDecimal);
        assertTrue(BigDecimal.valueOf(2).equals(typedValue));
    }

    /**
     * Next three tests check that DataConverterService should chose data converter accordingly to class
     * of the supplied value, when control is multi select query which hasn't data type
     * and JRParameter has class java.util.Collection without nested type.
     * @throws Exception
     */
    @Test
    public void ensureDataConverterAccordinglyToValueClassDate() throws Exception {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        calendar.set(2012, Calendar.NOVEMBER, 4, 19, 41, 17);

        Date value = new Date(calendar.getTimeInMillis());
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(value, inputControl, info);

        assertEquals("2012-11-04", formattedValue);
    }

    @Test
    public void ensureDataConverterAccordinglyToValueClassTimestamp() throws Exception {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.set(2012, Calendar.NOVEMBER, 4, 19, 41, 17);

        Timestamp value = new Timestamp(calendar.getTimeInMillis());
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(value, inputControl, info);

        assertEquals("2012-11-04 19:41", formattedValue);
    }

    @Test
    public void ensureDataConverterAccordinglyToValueClassTime() throws Exception {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.set(2012, Calendar.NOVEMBER, 4, 19, 41, 17);

        Time value = new Time(calendar.getTimeInMillis());
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "multiSelectICAndICInfoWithoutNestedType", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(value, inputControl, info);

        assertEquals("19:41", formattedValue);
    }

    /**
     * Next three tests check that null is properly formatted fot all three currently supported date types:
     * Date, Timestamp and Time. The result is empty string for all types.
     * @throws Exception
     */
    @Test
    public void ensureNullValueConvertedToEmptyStringForDate() throws Exception {
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "correctSingleValueDateICAndICInfo", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "correctSingleValueDateICAndICInfo", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(null, inputControl, info);

        assertEquals("", formattedValue);
    }

    @Test
    public void ensureNullValueConvertedToEmptyStringForTimestamp() throws Exception {
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "correctSingleValueTimestampICAndICInfo", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "correctSingleValueTimestampICAndICInfo", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(null, inputControl, info);

        assertEquals("", formattedValue);
    }

    @Test
    public void ensureNullValueConvertedToEmptyStringForTime() throws Exception {
        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "correctSingleValueTimeICAndICInfo", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "correctSingleValueTimeICAndICInfo", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(null, inputControl, info);

        assertEquals("", formattedValue);
    }

    /**
     * Next two tests check case when IC has data type Date but JRParameter has class java.sql.Timestamp.
     * @throws Exception
     */
    @Test
    public void ensureUseTimestampParserForICDateAndICInfoTimestampForFormatting() throws Exception {

        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.set(2012, Calendar.NOVEMBER, 7, 2, 10, 10);

        Timestamp value = new Timestamp(calendar.getTimeInMillis());

        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "singleValueDateAndICTimestamp", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "singleValueDateAndICTimestamp", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(value, inputControl, info);

        assertEquals("2012-11-07", formattedValue);
    }

    @Test
    public void ensureUseTimestampParserForICDateAndICInfoTimestampForParsing() throws Exception {

        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "singleValueDateAndICTimestamp", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "singleValueDateAndICTimestamp", applicationContext);

        Object convertedValue = dataConverterService.convertSingleValue("2012-11-07", inputControl, info);

        GregorianCalendar calendar = new GregorianCalendar(2012, Calendar.NOVEMBER, 7, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getDefault());
        Timestamp expected = new Timestamp(calendar.getTimeInMillis());

        assertEquals(expected, convertedValue);
    }

    /**
     * This case was introduced with RelativeDates feature, DataConverter should choose class of JRParameter
     * over class of supplied value if the JRParameter class is assignable from the value class.
     * @throws Exception
     */
    @Test
    public void ensureThatJRParameterClassIsChosenIfItsAssignableFromValueClass() throws Exception {

        TimestampRange dateRange = (TimestampRange) new DateRangeBuilder("DAY").set(Timestamp.class).toDateRange();

        ApplicationContext applicationContext = setUpApplicationContext(null, "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/converters/dataConverterService-fixtures.xml");
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        InputControl inputControl = getInputControl("orderDate", "jrParameterClassIsAssignableFromValueClass", applicationContext);
        ReportInputControlInformation info = getInputControlInfo("orderDate", "jrParameterClassIsAssignableFromValueClass", applicationContext);

        String formattedValue = dataConverterService.formatSingleValue(dateRange, inputControl, info);

        assertEquals("DAY", formattedValue);
    }

    @Test
    public void shouldConvertSingleValueToTime_WhenClientTimezoneIsDefault_returnTimeInClientTimezone() throws Exception {
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        DataTypeImpl timeDataType = new DataTypeImpl();
        timeDataType.setDataTypeType(DataType.TYPE_TIME);

        assertEquals(dataConverterService.convertSingleValue("11:11:00", timeDataType), Time.valueOf("11:11:00"));
    }

    @Test
    public void shouldFormatSingleValue_WhenClientTimezoneIsDefault_returnTimeStringInClientTimezone() throws Exception {
        setUpCachedRepositoryService();
        setUpGenericTypeProcessorRegistry();

        DataTypeImpl timeDataType = new DataTypeImpl();
        timeDataType.setDataTypeType(DataType.TYPE_TIME);

        assertEquals(dataConverterService.formatSingleValue(Time.valueOf("11:11:00")), "11:11");
    }

    private void setUpGenericTypeProcessorRegistry() throws Exception {
        final CalendarFormatProvider calendarFormatProvider = createCalendarFormatProvider();

        DataConverter<Date> dateConverter = new DateDataConverter();
        TimestampDataConverter timestampDataConverter = new TimestampDataConverter();
        DataConverter<Timestamp> dateTimeConverter = timestampDataConverter;
        DateParser<Timestamp> dateTimeParser = timestampDataConverter;
        DataConverter<Time> timeDataConverter = new TimeDataConverter();
        DataConverter<DateRange> dateRangeDataConverter = new DateRangeDataConverter();

        injectDependencyToPrivateField(dateConverter, "calendarFormatProvider", calendarFormatProvider);
        injectDependencyToPrivateField(timestampDataConverter, "calendarFormatProvider", calendarFormatProvider);
        injectDependencyToPrivateField(timeDataConverter, "calendarFormatProvider", calendarFormatProvider);
        injectDependencyToPrivateField(dateRangeDataConverter, "calendarFormatProvider", calendarFormatProvider);

        genericTypeProcessorRegistry.returns(dateConverter).getTypeProcessor(Date.class, DataConverter.class);
        genericTypeProcessorRegistry.returns(dateTimeConverter).getTypeProcessor(Timestamp.class, DataConverter.class);
        genericTypeProcessorRegistry.returns(dateTimeParser).getTypeProcessor(Timestamp.class, DateParser.class, false);
        genericTypeProcessorRegistry.returns(timeDataConverter).getTypeProcessor(Time.class, DataConverter.class);
        genericTypeProcessorRegistry.returns(dateRangeDataConverter).getTypeProcessor(DateRange.class, DataConverter.class);
    }

    private void setUpCachedRepositoryService() throws Exception {
        cachedRepositoryService.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                ResourceReference dataTypeRef = (ResourceReference)proxyInvocation.getArguments().get(1);
                return dataTypeRef.getLocalResource();
            }
        }).getResource(DataType.class, (ResourceReference)null);
    }
}
