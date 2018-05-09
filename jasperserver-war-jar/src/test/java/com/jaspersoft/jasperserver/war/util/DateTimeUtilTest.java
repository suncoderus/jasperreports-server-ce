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

package com.jaspersoft.jasperserver.war.util;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author schubar
 */
public class DateTimeUtilTest extends UnitilsJUnit4 {

    private Date expectedDate;

    private Date expectedDateTime;

    private java.sql.Date expectedSQLDate;

    private java.sql.Timestamp expectedTimestamp;

    private java.sql.Time expectedTime;


    @Before
    public void setUp() throws Exception {
        TimeZone tz = TimeZone.getTimeZone("Europe/Kiev");

        Calendar calendarDate = new GregorianCalendar(2012, 1, 28);
        calendarDate.setTimeZone(tz);

        Calendar calendarDateTime = new GregorianCalendar(2012, 1, 28, 2, 10, 15);
        calendarDate.setTimeZone(tz);

        Calendar calendarTime = new GregorianCalendar(1970, 0, 1, 17, 10, 15);
        calendarTime.setTimeZone(tz);

        this.expectedDate = calendarDate.getTime();
        this.expectedDateTime = calendarDateTime.getTime();
        this.expectedSQLDate = new java.sql.Date(this.expectedDate.getTime());
        this.expectedTimestamp = new Timestamp(this.expectedDateTime.getTime());
        this.expectedTime = new Time(calendarTime.getTimeInMillis());
    }

    @Test
    public void shouldParseDateInDefaultTimeZone() throws Exception {
        assertEquals(expected("2012-02-28", null),
                DateTimeUtil.parseDate("yyyy-MM-dd", "2012-02-28"));
    }

    @Test
    public void shouldParseDateInChicagoTimeZone() throws Exception {
        assertEquals(expected("2012-02-28 06:00:00", "GMT"),
                DateTimeUtil.parseDate("yyyy-MM-dd Z", "2012-02-28 -0600"));
    }

    @Test
    public void shouldParseDateWithTimeInDefaultTimeZone() throws Exception {
        assertEquals(expected("2012-02-28 02:10:15", null),
                DateTimeUtil.parseDate("yyyy-MM-dd HH:mm:ss", "2012-02-28 02:10:15"));
    }

    @Test
    public void shouldParseDateWithTimeInChicagoTimeZone() throws Exception {
        assertEquals(expected("2012-02-28 00:00:00", "GMT"),
                DateTimeUtil.parseDate("yyyy-MM-dd HH:mm:ss Z", "2012-02-27 18:00:00 -0600"));
    }

    @Test
    public void shouldParseTimestampInDefaultTimeZone() throws Exception {
        assertEquals(expectedTimestamp("2012-02-28 02:10:15", null),
                DateTimeUtil.parseTimestamp("yyyy-MM-dd HH:mm:ss", "2012-02-28 02:10:15"));
    }

    @Test
    public void shouldParseTimestampInChicagoTimeZone() throws Exception {
        assertEquals(expectedTimestamp("2012-02-28 00:10:15", "GMT"),
                DateTimeUtil.parseTimestamp("yyyy-MM-dd HH:mm:ss Z", "2012-02-27 18:10:15 -0600"));
    }

    @Test
    public void shouldParseSQLDateInDefaultTimeZone() throws Exception {
        assertEquals(expectedSQLDate("2012-02-28 02:10:15", null),
                DateTimeUtil.parseSQLDate("yyyy-MM-dd HH:mm:ss", "2012-02-28 02:10:15"));
    }

    @Test
    public void shouldParseSQLDateInChicagoTimeZone() throws Exception {
        assertEquals(expectedSQLDate("2012-02-28 00:10:15", "GMT"),
                DateTimeUtil.parseSQLDate("yyyy-MM-dd HH:mm:ss Z", "2012-02-27 18:10:15 -0600"));
    }

    @Test
    public void shouldParseTimeInDefaultTimeZone() throws Exception {
        assertEquals(expectedTime("17:10:15", null),
                DateTimeUtil.parseTime("HH:mm:ss", "17:10:15"));
    }

    @Test
    public void shouldParseTimeInChicagoTimeZone() throws Exception {
        assertEquals(expectedTime("14:10:15", "GMT"),
                DateTimeUtil.parseTime("HH:mm:ss Z", "8:10:15 -0600"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailParseDateIfValueNotMatchPattern() throws Exception {
        DateTimeUtil.parseDate("HH:mm:ss Z", "8:10:15");
    }

    private Date expected(String date, String timeZoneID) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(
                StringUtils.countMatches(date, " ") > 0  ? "yyyy-MM-dd HH:mm:ss" : "yyyy-MM-dd");
        if (timeZoneID != null) formatter.setTimeZone(TimeZone.getTimeZone(timeZoneID));
        return formatter.parse(date);
    }

    private Timestamp expectedTimestamp(String date, String timeZoneID) throws Exception  {
        return new Timestamp(expected(date, timeZoneID).getTime());
    }

    private java.sql.Date expectedSQLDate(String date, String timeZoneID) throws Exception  {
        return new java.sql.Date(expected(date, timeZoneID).getTime());
    }

    private Time expectedTime(String date, String timeZoneID) throws Exception  {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        if (timeZoneID != null) formatter.setTimeZone(TimeZone.getTimeZone(timeZoneID));
        return new Time(formatter.parse(date).getTime());
    }

}
