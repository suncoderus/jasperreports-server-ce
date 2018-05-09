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
import net.sf.jasperreports.types.date.FixedDate;
import net.sf.jasperreports.types.date.InvalidDateRangeExpressionException;
import org.junit.Test;

import java.sql.Time;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FixedDateTest extends BaseRelativeDateTest {

    @Test
    public void shouldCreateNewFixedDate() throws Exception {
        DateRange rd = new FixedDate("2012-08-01");
        assertNotNull(rd);
    }

    @Test(expected = InvalidDateRangeExpressionException.class)
    public void shouldFailToCreateFixedDateFromWrongExpression() throws Exception {
        new FixedDate("DAY-1", null, null);
    }

    @Test
    public void shouldReturnActualDate() throws Exception {
        DateRange rd = new FixedDate("2012-08-01");
        assertEquals(Date.class, rd.getStart().getClass());
        assertEquals(Date.class, rd.getEnd().getClass());
        assertEquals(date("2012-08-01"), rd.getStart());
        assertEquals(date("2012-08-01"), rd.getEnd());
    }

    @Test
    public void shouldCutTimePartIfDefaultConstructorWithoutPatternWasUsed() throws Exception {
        DateRange rd = new FixedDate("2012-08-01 12:05:34");
        assertEquals(dateTime("2012-08-01 00:00:00.000"), rd.getStart());
        assertEquals(dateTime("2012-08-01 00:00:00.000"), rd.getEnd());
    }

    @Test
    public void shouldApplyTimeZone() throws Exception {
        final TimeZone gmt = TimeZone.getTimeZone("GMT");
        FixedDate rd = new FixedDate("2012-08-01 12:05:34", gmt, "yyyy-MM-dd HH:mm:ss");

        assertEquals("2012-08-01 12:05:34.000", formatDate(rd.getStart(), gmt));
        assertEquals("2012-08-01 12:05:34.000", formatDate(rd.getEnd(), gmt));
    }

    @Test
    /**
     * NOTE: that FixedDate creates Time using default Timezone
     */
    public void shouldReturnActualTimeForTimePattern() throws Exception {
        DateRange rd = new FixedDate("00:00:00", null, "HH:mm:ss");
        assertEquals(dateTime("1970-01-01 00:00:00.000"), rd.getStart());
        assertEquals(dateTime("1970-01-01 00:00:00.000"), rd.getEnd());
    }

    @Test
    /**
     * NOTE: that FixedDate creates Time using default Timezone
     */
    public void shouldReturnActualDateForTimePatternPastMidtime() throws Exception {
        DateRange rd = new FixedDate("13:25:48", null, "HH:mm:ss");
        assertEquals(dateTime("1970-01-01 13:25:48.000"), rd.getStart());
        assertEquals(dateTime("1970-01-01 13:25:48.000"), rd.getEnd());
    }

    @Test
    /**
     * NOTE: that FixedDate creates Time using default Timezone
     */
    public void shouldReturnActualDateForTimePatternPastMidtimeIfFullPatternWasUsed() throws Exception {
        DateRange rd = new FixedDate("2012-08-01 13:25:48", null, "yyyy-MM-dd HH:mm:ss");
        assertEquals(dateTime("2012-08-01 13:25:48.000"), rd.getStart());
        assertEquals(dateTime("2012-08-01 13:25:48.000"), rd.getEnd());
    }

    @Test
    public void ensureDatePatternIsApplied() {
        DateRange dr = new FixedDate("12-08-02", null, "yy-MM-dd");
        assertEquals(date("2012-08-02"), dr.getStart());
        assertEquals(date("2012-08-02"), dr.getEnd());
    }

    @Test
    public void ensureDatePatternIsAppliedForDatetimeValue() {
        DateRange dr = new FixedDate("8/2/12 12:23:34", null, "M/d/yy HH:mm:ss");
        assertEquals(Date.class, dr.getStart().getClass());
        assertEquals(Date.class, dr.getEnd().getClass());
        assertEquals(dateTime("2012-08-02 12:23:34.000"), dr.getStart());
        assertEquals(dateTime("2012-08-02 12:23:34.000"), dr.getEnd());
    }

    @Test
    public void shouldReturnActualDateForDateObject() throws Exception {
        DateRange rd = new FixedDate(date("2012-08-01"));
        assertEquals(date("2012-08-01"), rd.getStart());
        assertEquals(date("2012-08-01"), rd.getEnd());
    }

    @Test
    public void shouldNotCutTimeComponentForDateObject() throws Exception {
        DateRange rd = new FixedDate(dateTime("2012-08-01 12:05:34.000"));
        assertEquals(dateTime("2012-08-01 12:05:34.000"), rd.getStart());
        assertEquals(dateTime("2012-08-01 12:05:34.000"), rd.getEnd());
    }

    @Test
    public void shouldHoldPassedValue() throws Exception {
        final Time time = new Time(dateTime("2012-08-01 12:05:34.000").getTime());
        DateRange rd = new FixedDate(time);
        assertEquals(time, rd.getStart());
        assertEquals(time, rd.getEnd());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureNullCouldNotBeUsedAsDateValue() throws Exception {
        new FixedDate((Date)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureNullCouldNotBeUsedAsExpression() throws Exception {
        new FixedDate((String)null);
    }
}
