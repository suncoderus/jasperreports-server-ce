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

import com.jaspersoft.jasperserver.api.common.util.rd.DateRangeFactory;
import net.sf.jasperreports.types.date.FixedTimestamp;
import net.sf.jasperreports.types.date.RelativeTimestampRange;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import net.sf.jasperreports.types.date.DateRange;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TimestampRangeConverterTest extends UnitilsJUnit4 {

    @Test
    public void ensureRelativeDateRangeReturnedForRangeExpressions() throws Exception {
        TimestampRangeDataConverter dateRangeConverter = getTimestampRangeConverter();
        DateRange dr = dateRangeConverter.stringToValue("WEEK-1");
        assertNotNull(dr);
        assertEquals(RelativeTimestampRange.class, dr.getClass());
    }

    @Test
    public void ensureSingleRelativeReturnedDateForDateExpressions() throws Exception {
        TimestampRangeDataConverter dateRangeConverter = getTimestampRangeConverter();
        DateRange dr = dateRangeConverter.stringToValue("DAY+1");
        assertNotNull(dr);
        assertEquals(RelativeTimestampRange.class, dr.getClass());
    }

    @Test
    public void ensureFixedDateReturnedForDateExpressions() throws Exception {
        TimestampRangeDataConverter dateRangeConverter = getTimestampRangeConverter();
        DateRange dr = dateRangeConverter.stringToValue("2012-07-01 12:34");
        assertNotNull(dr);
        assertEquals(FixedTimestamp.class, dr.getClass());

        assertEquals(new GregorianCalendar(2012, Calendar.JULY, 1, 12, 34, 0).getTime().getTime(), dr.getStart().getTime());
        assertEquals(new GregorianCalendar(2012, Calendar.JULY, 1, 12, 34, 0).getTime().getTime(), dr.getEnd().getTime());
    }

    @Test
    public void ensureFixedDateReturnedForDatetimeExpressions() throws Exception {
        TimestampRangeDataConverter dateRangeConverter = getTimestampRangeConverter();
        DateRange dr = dateRangeConverter.stringToValue("2012-07-01 12:20");
        assertNotNull(dr);
        assertEquals(FixedTimestamp.class, dr.getClass());

        assertEquals(new GregorianCalendar(2012, Calendar.JULY, 1, 12, 20).getTime(), dr.getStart());
        assertEquals(new GregorianCalendar(2012, Calendar.JULY, 1, 12, 20).getTime(), dr.getEnd());
    }

    @Test
    public void ensureNullReturnedForEmptyString() throws Exception {
        TimestampRangeDataConverter dateRangeConverter = getTimestampRangeConverter();
        DateRange dr = dateRangeConverter.stringToValue("");
        assertNull(dr);
    }

    @Test
    public void ensureEmptyStringReturnedForNull() throws Exception {
        TimestampRangeDataConverter dateRangeConverter = getTimestampRangeConverter();
        String str = dateRangeConverter.valueToString(null);
        assertEquals(str, "");
    }

    @Test
    public void ensureRelativeDateRangeExpressionMatches() throws Exception {
        TimestampRangeDataConverter dateRangeConverter = getTimestampRangeConverter();
        String expression = "WEEK-1";
        String str = dateRangeConverter.valueToString((RelativeTimestampRange) DateRangeFactory
                .getInstance(expression, Timestamp.class, null));
        assertEquals(expression, str);
    }

    @Test
    public void ensureSingleRelativeDateExpressionMatches() throws Exception {
        TimestampRangeDataConverter dateRangeConverter = getTimestampRangeConverter();
        String expression = "DAY-100";
        String str = dateRangeConverter.valueToString((RelativeTimestampRange) DateRangeFactory
                .getInstance(expression, Timestamp.class, null));
        assertEquals(expression, str);
    }

    @Test
    public void ensureFixedDateTimeExpressionMatches() throws Exception {
        TimestampRangeDataConverter dateRangeConverter = getTimestampRangeConverter();
        String expression = "2012-07-12 12:20:01";
        String dateConverterFormat = "2012-07-12 12:20";
        String str = dateRangeConverter.valueToString((FixedTimestamp) DateRangeFactory
                .getInstance(expression, Timestamp.class, null));
        assertEquals(dateConverterFormat, str);
    }

    private TimestampRangeDataConverter getTimestampRangeConverter() {
        TimestampRangeDataConverter dateRangeConverter = new TimestampRangeDataConverter();
        injectDependencyToPrivateField(dateRangeConverter, "calendarFormatProvider", createCalendarFormatProvider());

        return dateRangeConverter;
    }

    public static CalendarFormatProvider createCalendarFormatProvider() {
        Mock<CalendarFormatProvider> calendarFormatProviderMock = MockUnitils.createMock(CalendarFormatProvider.class);
        calendarFormatProviderMock.returns(new SimpleDateFormat("yyyy-MM-dd")).getDateFormat();
        calendarFormatProviderMock.returns(new SimpleDateFormat("yyyy-MM-dd HH:mm")).getDatetimeFormat();

        return calendarFormatProviderMock.getMock();
    }

    public static void injectDependencyToPrivateField(Object holder, String name, Object value) {
        Field field = ReflectionUtils.getFieldWithName(holder.getClass(), name, false);
        ReflectionUtils.setFieldValue(holder, field, value);
    }

}
