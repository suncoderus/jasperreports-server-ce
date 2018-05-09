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
package com.jaspersoft.jasperserver.export.modules.common.rd;

import net.sf.jasperreports.types.date.DateRangeBuilder;
import net.sf.jasperreports.types.date.DateRangeExpression;
import net.sf.jasperreports.types.date.FixedDate;
import net.sf.jasperreports.types.date.FixedTimestamp;
import net.sf.jasperreports.types.date.RelativeDateRange;
import net.sf.jasperreports.types.date.RelativeTimestampRange;
import net.sf.jasperreports.types.date.DateRange;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class DateRangeDTOTest {

    @Test
    public void testDateRangeDTOInstantiationFromDateRangeByExpression() {
        final String expression = "WEEK";
        DateRangeDTO dto = new DateRangeDTO(new DateRangeBuilder(expression).toDateRange());
        DateRange dateRange = dto.toDateRange();

        assertEquals(expression, dto.getExpression());
        assertEquals(Date.class.getName(), dto.getValueClass());
        assertNull(dto.getDate());

        assertTrue(dateRange instanceof RelativeDateRange);
        assertEquals(expression, ((DateRangeExpression)dateRange).getExpression());
    }

    @Test
    public void testDateRangeDTOInstantiationFromDateRangeByDate() {
        final Date date = new Date(234234234L);
        DateRangeDTO dto = new DateRangeDTO(new DateRangeBuilder(date).toDateRange());
        DateRange dateRange = dto.toDateRange();

        assertEquals(date, dto.getDate());
        assertEquals(Date.class.getName(), dto.getValueClass());
        assertNull(dto.getExpression());

        assertTrue(dateRange instanceof FixedDate);
        assertEquals(date, dateRange.getStart());
    }

    @Test
    public void testDateRangeDTOInstantiationFromTimestampRangeByExpression() {
        final String expression = "WEEK";
        DateRangeDTO dto = new DateRangeDTO(new DateRangeBuilder(expression).set(Timestamp.class).toDateRange());
        DateRange dateRange = dto.toDateRange();

        assertEquals(expression, dto.getExpression());
        assertEquals(Timestamp.class.getName(), dto.getValueClass());
        assertNull(dto.getDate());

        assertTrue(dateRange instanceof RelativeTimestampRange);
        assertEquals(expression, ((DateRangeExpression)dateRange).getExpression());
    }

    @Test
    public void testDateRangeDTOInstantiationFromTimestampRangeByDate() {
        final Timestamp ts = new Timestamp(234234234L);
        DateRangeDTO dto = new DateRangeDTO(new DateRangeBuilder(ts).toDateRange());
        DateRange dateRange = dto.toDateRange();

        assertEquals(ts, dto.getDate());
        assertEquals(Timestamp.class.getName(), dto.getValueClass());
        assertNull(dto.getExpression());

        assertTrue(dateRange instanceof FixedTimestamp);
        assertEquals(ts, dateRange.getStart());
    }
}
