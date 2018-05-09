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

package com.jaspersoft.jasperserver.api.common.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Date utilities class.
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class DateUtils {

    public static boolean isToday(Date date) {
        return isSameDay(date, new Date());
    }

    public static boolean isYesterday(Date date) {
        return isSameDay(date, rollDay(new Date(), -1));
    }

    public static boolean isThisYear(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        Calendar cal2 = Calendar.getInstance();
        
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    private static Date rollDay(Date date, long amount) {
        long millisecondsInDay = 24 * 60 * 60 * 1000;
        long rolledTime = date.getTime() + amount * millisecondsInDay;
        return new Date(rolledTime);
    }
}
