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
import java.util.TimeZone;

/**
 * @author Anton Fomin
 * @author StasChubar
 * @version $Id$
 */
public abstract class TimeZoneContextHolder {
    public static final ThreadLocal<TimeZone> threadLocalContext = new ThreadLocal<TimeZone>();
    public static final ThreadLocal<TimeZone> inheritableThreadLocalContext = new InheritableThreadLocal<TimeZone>();

    public static void setTimeZone(TimeZone timeZone) {
        threadLocalContext.set(timeZone);
        inheritableThreadLocalContext.set(timeZone);
    }

    public static void resetTimeZone() {
        threadLocalContext.remove();
        inheritableThreadLocalContext.remove();
    }

    public static TimeZone getTimeZone() {
        TimeZone tz = threadLocalContext.get();
        if (tz == null) {
            tz = inheritableThreadLocalContext.get();
        }
        return (tz != null) ? tz : Calendar.getInstance().getTimeZone();
    }
}