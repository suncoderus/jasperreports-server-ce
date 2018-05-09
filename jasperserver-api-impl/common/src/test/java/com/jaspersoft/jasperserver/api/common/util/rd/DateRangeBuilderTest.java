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
import net.sf.jasperreports.types.date.DateRangeBuilder;
import net.sf.jasperreports.types.date.FixedDate;
import net.sf.jasperreports.types.date.FixedTimestamp;
import net.sf.jasperreports.types.date.RelativeDateRange;
import net.sf.jasperreports.types.date.RelativeTimestampRange;
import org.junit.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DateRangeBuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void ensureNullCannotBeUsedAsExpression() {
        DateRangeFactory.getInstance((String)null);
    }

    @Test
    public void ensureFixedDateCreatedFromProperPattern() {
        DateRange dr = new DateRangeBuilder("2012-08-01").toDateRange();
        assertNotNull(dr);
        assertEquals(FixedDate.class, dr.getClass());
    }

    @Test
    public void ensureFixedTimestampCreatedIfValueClassSet() {
        DateRange dr = new DateRangeBuilder("2012-08-01").set(Timestamp.class).set("yyyy-MM-dd").toDateRange();
        assertNotNull(dr);
        assertEquals(FixedTimestamp.class, dr.getClass());
    }

    @Test
    public void ensureFixedTimestampCreatedFromTimestampLikePattern() {
        DateRange dr = new DateRangeBuilder("12:34").set("HH:mm").toDateRange();
        assertNotNull(dr);
        assertEquals(FixedTimestamp.class, dr.getClass());
    }

    @Test
    public void ensureFixedTimestampCreatedFromProperPattern() {
        DateRange dr = new DateRangeBuilder("2012-08-01 12:34:56").toDateRange();
        assertNotNull(dr);
        assertEquals(FixedTimestamp.class, dr.getClass());
    }

    @Test
    public void ensureSingleRelativeDateCreatedFromProperPattern() {
        DateRange dr = new DateRangeBuilder("DAY-1").toDateRange();
        assertNotNull(dr);
        assertEquals(RelativeDateRange.class, dr.getClass());
    }

    @Test
    public void ensureRelativeDateRangeCreatedFromProperPattern() {
        DateRange dr = new DateRangeBuilder("WEEK-1").toDateRange();
        assertNotNull(dr);
        assertEquals(RelativeDateRange.class, dr.getClass());
    }

    @Test
    public void ensureProperFixedDateIsCreatedFromPassedDate() {
        DateRange dr = new DateRangeBuilder((new GregorianCalendar(2012, 7, 2).getTime())).toDateRange();
        assertEquals(FixedDate.class, dr.getClass());
        assertEquals(new GregorianCalendar(2012, 7, 2).getTime(), dr.getStart());
    }

    @Test
    public void ensureValueClassMattersEvenIfDateInstancePassed() {
        DateRange dr = new DateRangeBuilder((new GregorianCalendar(2012, 7, 2).getTime())).set(Timestamp.class).toDateRange();
        assertEquals(FixedTimestamp.class, dr.getClass());
        assertEquals(new GregorianCalendar(2012, 7, 2).getTime().getTime(), dr.getStart().getTime());
    }

    @Test
    public void ensureValueClassMattersEvenIfTimestampInstancePassed() {
        DateRange dr = new DateRangeBuilder(new Timestamp(new GregorianCalendar(2012, 7, 2).getTime().getTime())).set(Date.class).toDateRange();
        assertEquals(FixedDate.class, dr.getClass());
        assertEquals(new GregorianCalendar(2012, 7, 2).getTime().getTime(), dr.getStart().getTime());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureExceptionIsThrownWhenNullWasUsedAsExpression() {
        new DateRangeBuilder((String)null).toDateRange();
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureExceptionIsThrownWhenNullWasUsedAsDateValue() {
        new DateRangeBuilder((Date)null).toDateRange();
    }

    @Test
    public void ensureUSLocaleIsUsedToDetermineWeekStart() {
        DateRange dr = new DateRangeBuilder("WEEK").set(Locale.US).toDateRange();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dr.getStart());
        int startDay = calendar.get(Calendar.DAY_OF_WEEK);

        calendar.setTime(dr.getEnd());
        int endDay = calendar.get(Calendar.DAY_OF_WEEK);

        assertEquals(Calendar.SUNDAY, startDay);
        assertEquals(Calendar.SATURDAY, endDay);
    }

    @Test
    public void ensureFranceLocaleIsUsedToDetermineWeekStart() {
        DateRange dr = new DateRangeBuilder("WEEK").set(Locale.FRANCE).toDateRange();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dr.getStart());
        int startDay = calendar.get(Calendar.DAY_OF_WEEK);

        calendar.setTime(dr.getEnd());
        int endDay = calendar.get(Calendar.DAY_OF_WEEK);

        assertEquals(Calendar.MONDAY, startDay);
        assertEquals(Calendar.SUNDAY, endDay);
    }

    @Test
    public void ensureRelativeTimestampIsCreatedForTimestampPattern() {
        DateRange dr = new DateRangeBuilder("DAY").set(Timestamp.class).toDateRange();
        assertEquals(RelativeTimestampRange.class, dr.getClass());
    }

    @Test
    public void ensureRelativeTimeIsCreatedForTimePattern() {
        DateRange dr = new DateRangeBuilder("DAY").set(Time.class).toDateRange();
        assertEquals(RelativeDateRange.class, dr.getClass());
    }

}
