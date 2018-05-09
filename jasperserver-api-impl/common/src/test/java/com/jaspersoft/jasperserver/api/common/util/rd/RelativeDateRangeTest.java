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

package com.jaspersoft.jasperserver.api.common.util.rd;

import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.RelativeDateRange;
import net.sf.jasperreports.types.date.DateRangeExpression;
import net.sf.jasperreports.types.date.InvalidDateRangeExpressionException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

@Ignore //Uncomment if test is failed under some platforms
public class RelativeDateRangeTest extends BaseRelativeDateTest {

    @Test(expected = InvalidDateRangeExpressionException.class)
    public void shouldFailToCreateRelativeDateRangeFromWrongExpression() throws Exception {
        new RelativeDateRange("2012-08-01");
    }


    // Tests for "DAY" expression

    @Test
    public void shouldReturnDateInPast() throws Exception {
        DateRange rd = new RelativeDateRange("DAY-3");
        assertEquals(Date.class, rd.getStart().getClass());
        assertEquals(Date.class, rd.getEnd().getClass());
        assertEquals(day(-3), rd.getStart());
        assertEquals(lastMomentOfDay(-3), rd.getEnd());
        assertEquals("DAY-3", ((DateRangeExpression) rd).getExpression());
    }

    @Test
    public void shouldReturnCurrentDay() throws Exception {
        DateRange rd = new RelativeDateRange("DAY");
        assertEquals(day(0), rd.getStart());
        assertEquals(lastMomentOfDay(0), rd.getEnd());
        assertEquals("DAY", ((DateRangeExpression) rd).getExpression());
    }

    @Test
    public void shouldReturnDayInFuture() throws Exception {
        DateRange rd = new RelativeDateRange("DAY+32");
        assertEquals(day(32), rd.getStart());
        assertEquals(lastMomentOfDay(32), rd.getEnd());
        assertEquals("DAY+32", ((DateRangeExpression) rd).getExpression());
    }

    @Test
    public void shouldApplyTimeZoneForDay() throws Exception {
        final TimeZone gmt = TimeZone.getTimeZone("GMT");
        TestRelativeDateRange rd = new TestRelativeDateRange("DAY", gmt);

        assertEquals("2012-08-01 00:00:00.000", formatDate(rd.getStart(), gmt));
        assertEquals("2012-08-01 23:59:59.999", formatDate(rd.getEnd(), gmt));
    }


    // Tests for "WEEK" expression

    @Test
    public void shouldReturnCurrentWeek() throws Exception {
        DateRange rd = new TestRelativeDateRange("WEEK");
        assertEquals(date("2012-07-30"), rd.getStart());
        assertEquals(dateTime("2012-08-05 23:59:59.999"), rd.getEnd());
    }

    @Test
    public void shouldReturnWeekInFuture() throws Exception {
        TestRelativeDateRange rd = new TestRelativeDateRange("WEEK+3");
        assertEquals(date("2012-08-20"), rd.getStart());
        assertEquals(dateTime("2012-08-26 23:59:59.999"), rd.getEnd());

        rd = new TestRelativeDateRange("WEEK+50");
        assertEquals(date("2013-07-15"), rd.getStart());
        assertEquals(dateTime("2013-07-21 23:59:59.999"), rd.getEnd());
    }

    @Test
    public void shouldReturnWeekInPast() throws Exception {
        DateRange rd = new TestRelativeDateRange("WEEK-1");
        assertEquals(date("2012-07-23"), rd.getStart());
        assertEquals(dateTime("2012-07-29 23:59:59.999"), rd.getEnd());

        rd = new TestRelativeDateRange("WEEK-63");
        assertEquals(date("2011-05-16"), rd.getStart());
        assertEquals(dateTime("2011-05-22 23:59:59.999"), rd.getEnd());
    }

    @Test
    public void shouldApplyTimeZoneForWeek() throws Exception {
        TimeZone timeZone = TimeZone.getTimeZone("GMT-08");
        TestRelativeDateRange rd = new TestRelativeDateRange("WEEK-63", timeZone);

        assertEquals("2011-05-16 00:00:00.000", formatDate(rd.getStart(), timeZone));
        assertEquals("2011-05-22 23:59:59.999", formatDate(rd.getEnd(), timeZone));
    }


    // Tests for "MONTH" expression

    @Test
    public void shouldReturnCurrentMonth() throws Exception {
        DateRange rd = new TestRelativeDateRange("MONTH");
        assertEquals(date("2012-08-01"), rd.getStart());
        assertEquals(dateTime("2012-08-31 23:59:59.999"), rd.getEnd());
    }

    @Test
    public void shouldReturnMonthInFuture() throws Exception {
        TestRelativeDateRange rd = new TestRelativeDateRange("MONTH+3");
        assertEquals(date("2012-11-01"), rd.getStart());
        assertEquals(dateTime("2012-11-30 23:59:59.999"), rd.getEnd());

        rd = new TestRelativeDateRange("MONTH+42");
        rd.setDate(date("2012-08-31"));

        assertEquals(date("2016-02-01"), rd.getStart());
        assertEquals(dateTime("2016-02-29 23:59:59.999"), rd.getEnd());
    }

    @Test
    public void shouldReturnMonthInPast() throws Exception {
        DateRange rd = new TestRelativeDateRange("MONTH-1");
        assertEquals(date("2012-07-01"), rd.getStart());
        assertEquals(dateTime("2012-07-31 23:59:59.999"), rd.getEnd());

        rd = new TestRelativeDateRange("MONTH-31");
        assertEquals(date("2010-01-01"), rd.getStart());
        assertEquals(dateTime("2010-01-31 23:59:59.999"), rd.getEnd());
    }

    // Tests for "QUARTER" expression

    @Test
    public void shouldReturnCurrentQuarter() throws Exception {
        DateRange rd = new TestRelativeDateRange("QUARTER");
        assertEquals(date("2012-07-01"), rd.getStart());
        assertEquals(dateTime("2012-09-30 23:59:59.999"), rd.getEnd());
    }

    @Test
    public void shouldReturnQuarterInFuture() throws Exception {
        DateRange rd = new TestRelativeDateRange("QUARTER+3");
        assertEquals(date("2013-04-01"), rd.getStart());
        assertEquals(dateTime("2013-06-30 23:59:59.999"), rd.getEnd());

        rd = new TestRelativeDateRange("QUARTER+13");
        assertEquals(date("2015-10-01"), rd.getStart());
        assertEquals(dateTime("2015-12-31 23:59:59.999"), rd.getEnd());
    }

    @Test
    public void shouldReturnQuarterInPast() throws Exception {
        DateRange rd = new TestRelativeDateRange("QUARTER-1");
        assertEquals(date("2012-04-01"), rd.getStart());
        assertEquals(dateTime("2012-06-30 23:59:59.999"), rd.getEnd());

        rd = new TestRelativeDateRange("QUARTER-22");
        assertEquals(date("2007-01-01"), rd.getStart());
        assertEquals(dateTime("2007-03-31 23:59:59.999"), rd.getEnd());
    }


    // Tests for "SEMI" expression

    @Test
    public void shouldReturnCurrentSemi() throws Exception {
        DateRange rd = new TestRelativeDateRange("SEMI");
        assertEquals(date("2012-07-01"), rd.getStart());
        assertEquals(dateTime("2012-12-31 23:59:59.999"), rd.getEnd());
    }

    @Test
    public void shouldReturnSemiInFuture() throws Exception {
        DateRange rd = new TestRelativeDateRange("SEMI+3");
        assertEquals(date("2014-01-01"), rd.getStart());
        assertEquals(dateTime("2014-06-30 23:59:59.999"), rd.getEnd());

        rd = new TestRelativeDateRange("SEMI+13");
        assertEquals(date("2019-01-01"), rd.getStart());
        assertEquals(dateTime("2019-06-30 23:59:59.999"), rd.getEnd());
    }

    @Test
    public void shouldReturnSemiInPast() throws Exception {
        DateRange rd = new TestRelativeDateRange("SEMI-1");
        assertEquals(date("2012-01-01"), rd.getStart());
        assertEquals(dateTime("2012-06-30 23:59:59.999"), rd.getEnd());

        rd = new TestRelativeDateRange("SEMI-24");
        assertEquals(date("2000-07-01"), rd.getStart());
        assertEquals(dateTime("2000-12-31 23:59:59.999"), rd.getEnd());
    }

    // Tests for "YEAR" expression

    @Test
    public void shouldReturnCurrentYear() throws Exception {
        DateRange rd = new TestRelativeDateRange("YEAR");
        assertEquals(date("2012-01-01"), rd.getStart());
        assertEquals(dateTime("2012-12-31 23:59:59.999"), rd.getEnd());
    }

    @Test
    public void shouldReturnYearInFuture() throws Exception {
        DateRange rd = new TestRelativeDateRange("YEAR+3");
        assertEquals(date("2015-01-01"), rd.getStart());
        assertEquals(dateTime("2015-12-31 23:59:59.999"), rd.getEnd());
    }

    @Test
    public void shouldReturnYearInPast() throws Exception {
        DateRange rd = new TestRelativeDateRange("YEAR-1");
        assertEquals(date("2011-01-01"), rd.getStart());
        assertEquals(dateTime("2011-12-31 23:59:59.999"), rd.getEnd());
    }


    // Tests for localized week start

    @Test
    public void ensureSundayIsUsedAsWeekStartForUSLocale() {
        DateRange dr = new TestRelativeDateRange("WEEK", Calendar.SUNDAY);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dr.getStart());
        int dayOfWeekStart = calendar.get(Calendar.DAY_OF_WEEK);

        calendar = new GregorianCalendar();
        calendar.setTime(dr.getEnd());
        int dayOfWeekEnd = calendar.get(Calendar.DAY_OF_WEEK);

        assertEquals(Calendar.SUNDAY, dayOfWeekStart);
        assertEquals(Calendar.SATURDAY, dayOfWeekEnd);
    }

    @Test
    public void ensureMondayIsUsedAsWeekStartForFranceLocale() {
        DateRange dr = new TestRelativeDateRange("WEEK", Calendar.MONDAY);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dr.getStart());
        int dayOfWeekStart = calendar.get(Calendar.DAY_OF_WEEK);

        calendar = new GregorianCalendar();
        calendar.setTime(dr.getEnd());
        int dayOfWeekEnd = calendar.get(Calendar.DAY_OF_WEEK);

        assertEquals(Calendar.MONDAY, dayOfWeekStart);
        assertEquals(Calendar.SUNDAY, dayOfWeekEnd);
    }

    @Test
    public void ensureSundayIsWeekStartDayInUS() {
        TestRelativeDateRange dr = new TestRelativeDateRange("WEEK", Calendar.SUNDAY);
        dr.setDate(new GregorianCalendar(2012, Calendar.AUGUST, 5, 0, 0, 0).getTime());

        assertEquals(date("2012-08-05"), dr.getStart());
        assertEquals(dateTime("2012-08-11 23:59:59.999"), dr.getEnd());
    }

    @Test
    public void ensureSundayIsWeekEndDayInFrance() {
        TestRelativeDateRange dr = new TestRelativeDateRange("WEEK", Calendar.MONDAY);
        dr.setDate(new GregorianCalendar(2012, Calendar.AUGUST, 5, 0, 0, 0).getTime());

        assertEquals(date("2012-07-30"), dr.getStart());
        assertEquals(dateTime("2012-08-05 23:59:59.999"), dr.getEnd());
    }

    @Test
    public void ensureSundayIsWeekStartDayInUSEvenWithCalculation() {
        TestRelativeDateRange dr = new TestRelativeDateRange("WEEK+1", Calendar.SUNDAY);
        dr.setDate(new GregorianCalendar(2012, Calendar.AUGUST, 5, 0, 0, 0).getTime());

        assertEquals(date("2012-08-12"), dr.getStart());
        assertEquals(dateTime("2012-08-18 23:59:59.999"), dr.getEnd());
    }

    @Test
    public void ensureSartudayIsWeekEndDayInUS() {
        TestRelativeDateRange dr = new TestRelativeDateRange("WEEK-1", Calendar.SUNDAY);
        dr.setDate(new GregorianCalendar(2012, Calendar.AUGUST, 4, 0, 0, 0).getTime());

        assertEquals(date("2012-07-22"), dr.getStart());
        assertEquals(dateTime("2012-07-28 23:59:59.999"), dr.getEnd());
    }

    @Test
    public void ensureMondayIsWeekStartDayInFrance() {
        TestRelativeDateRange dr = new TestRelativeDateRange("WEEK+1", Calendar.MONDAY);
        dr.setDate(new GregorianCalendar(2012, Calendar.JULY, 30, 0, 0, 0).getTime());

        assertEquals(date("2012-08-06"), dr.getStart());
        assertEquals(dateTime("2012-08-12 23:59:59.999"), dr.getEnd());
    }

    @Test
    public void ensureSundayUsedAsWeekStartFromConfig() {
        TestRelativeDateRange dr = new TestRelativeDateRange("WEEK", "relativedate_sunday.properties");
        dr.setDate(new GregorianCalendar(2012, Calendar.AUGUST, 5, 0, 0, 0).getTime());

        assertEquals(date("2012-08-05"), dr.getStart());
        assertEquals(dateTime("2012-08-11 23:59:59.999"), dr.getEnd());
    }

    @Test
    public void ensureMondayUsedAsWeekStartFromConfig() {
        TestRelativeDateRange dr = new TestRelativeDateRange("WEEK+1", "relativedate_monday.properties");
        dr.setDate(new GregorianCalendar(2012, Calendar.AUGUST, 5, 0, 0, 0).getTime());

        assertEquals(date("2012-08-06"), dr.getStart());
        assertEquals(dateTime("2012-08-12 23:59:59.999"), dr.getEnd());
    }

    private class TestRelativeDateRange extends RelativeDateRange {
        private Date date = new GregorianCalendar(2012, Calendar.AUGUST, 1, 0, 0, 0).getTime();
        private String relativeDatePropsFile;

        TestRelativeDateRange(String expression) {
            super(expression);
        }

        private TestRelativeDateRange(String expression, int weekStart) {
            super(expression, null, weekStart);
        }

        private TestRelativeDateRange(String expression, String relativeDatePropsFile) {
            super(expression);
            setProperties(null);
            this.relativeDatePropsFile = relativeDatePropsFile;
        }

        private TestRelativeDateRange(String expression, TimeZone timeZone) {
            super(expression, timeZone, null);
        }

        public void setDate(Date date) {
            this.date = date;
        }

        protected Date getCurrentDate() {
            return this.date;
        }

        @Override
        protected String getPropertiesFileName() {
            if (relativeDatePropsFile != null) {
                return relativeDatePropsFile;
            }

            return super.getPropertiesFileName();
        }
    }
}
