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

package com.jaspersoft.jasperserver.dto.adhoc;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

import static org.joda.time.DateTimeZone.forTimeZone;

public class DateFixtures {
    public static TimeZone JVM_TZ = TimeZone.getDefault();

    public static Date DATE_1961_08_26 = new DateTime("1961-08-26").toDate();
    public static Date DATE_1913_01_01 = new DateTime("1913-01-01").toDate();
    public static Date DATE_1979_01_01 = new DateTime("1979-01-01").toDate();
    public static Date DATE_1912_07_09 = new DateTime("1912-07-09").toDate();
    public static Date DATE_1979_06_23 = new DateTime("1979-06-23").toDate();

    public static Timestamp DATE_2009_12_1T0_0_0 =
            new Timestamp(new DateTime(2009, 12, 1, 0, 0, 0, forTimeZone(JVM_TZ)).getMillis());

    public static Timestamp DATE_2009_1_1T0_0_0 =
            new Timestamp(new DateTime(2009, 1, 1, 0, 0, 0, forTimeZone(JVM_TZ)).getMillis());

    public static Timestamp DATE_2010_1_1T0_0_0 =
            new Timestamp(new DateTime(2010, 1, 1, 0, 0, 0, forTimeZone(JVM_TZ)).getMillis());

    public static Timestamp DATE_2012_1_1T0_0_0 =
            new Timestamp(new DateTime(2012, 1, 1, 0, 0, 0, forTimeZone(JVM_TZ)).getMillis());


    public static Time TIME_3_33 =
            new Time(new LocalTime(3, 33).toDateTimeToday().withDate(1970, 1, 1).getMillis());

    public static Time TIME_15_00 =
            new Time(new LocalTime(15, 0).toDateTimeToday().withDate(1970, 1, 1).getMillis());


}