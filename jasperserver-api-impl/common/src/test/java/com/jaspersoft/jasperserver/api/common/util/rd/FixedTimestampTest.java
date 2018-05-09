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

import net.sf.jasperreports.types.date.InvalidDateRangeExpressionException;
import net.sf.jasperreports.types.date.FixedTimestamp;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FixedTimestampTest extends BaseRelativeDateTest {

    @Test
    public void shouldCreateNewFixedTimestamp() throws Exception {
        FixedTimestamp rd = new FixedTimestamp("2012-08-01 12:23:45");
        assertNotNull(rd);
    }

    @Test(expected = InvalidDateRangeExpressionException.class)
    public void shouldFailToCreateFixedTimestampFromWrongExpression() throws Exception {
        new FixedTimestamp("DAY-1", null, null);
    }

    @Test
    public void shouldReturnActualDate() throws Exception {
        FixedTimestamp rd = new FixedTimestamp("2012-08-01 12:23:45");
        assertEquals(Timestamp.class, rd.getStart().getClass());
        assertEquals(Timestamp.class, rd.getEnd().getClass());
        assertEquals(dateTime("2012-08-01 12:23:45.000"), rd.getStart());
        assertEquals(dateTime("2012-08-01 12:23:45.000"), rd.getEnd());
    }

    @Test
    public void shouldApplyTimeZone() throws Exception {
        final TimeZone gmt = TimeZone.getTimeZone("GMT");
        FixedTimestamp rd = new FixedTimestamp("2012-08-01 12:23:45", gmt, null);

        assertEquals("2012-08-01 12:23:45.000", formatDate(rd.getStart(), gmt));
        assertEquals("2012-08-01 12:23:45.000", formatDate(rd.getEnd(), gmt));
    }

    @Test
    /**
     * NOTE: that FixedDate creates Time using default Timezone
     */
    public void shouldReturnActualDateForTimePatternPastMidtimeIfFullPatternWasUsed() throws Exception {
        FixedTimestamp rd = new FixedTimestamp("2012-08-01 13/25/48", null, "yyyy-MM-dd HH/mm/ss");
        assertEquals(dateTime("2012-08-01 13:25:48.000"), rd.getStart());
        assertEquals(dateTime("2012-08-01 13:25:48.000"), rd.getEnd());
    }

    @Test
    public void shouldReturnActualDateForDateObject() throws Exception {
        FixedTimestamp rd = new FixedTimestamp(new Timestamp(dateTime("2012-08-01 12:23:45.000").getTime()));
        assertEquals(dateTime("2012-08-01 12:23:45.000"), rd.getStart());
        assertEquals(dateTime("2012-08-01 12:23:45.000"), rd.getEnd());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureNullCouldNotBeUsedAsDateValue() throws Exception {
        new FixedTimestamp((Timestamp)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureNullCouldNotBeUsedAsExpression() throws Exception {
        new FixedTimestamp((String)null);
    }
}
