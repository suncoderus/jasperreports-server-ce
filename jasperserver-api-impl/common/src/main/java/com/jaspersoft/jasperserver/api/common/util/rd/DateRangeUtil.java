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
import net.sf.jasperreports.types.date.DateRangeExpression;

/**
 * <p>Utility class to deal with date ranges</p>
 *
 * @author Sergey Prilukin
 * @version $Id: DateRangeUtil.java 25092 2012-10-03 15:38:24Z tdanciu $
 */
public class DateRangeUtil {

    /**
     * Returns date range string expression if it is supported
     *
     * @param dateRange instance of {@link DateRange}
     * @return string which represents expression for date range
     *  or {@code null} if instance of date range does not provide expression
     */
    public static String getExpression(DateRange dateRange) {
        if (dateRange instanceof DateRangeExpression) {
            return ((DateRangeExpression)dateRange).getExpression();
        } else {
            return null;
        }
    }
}
