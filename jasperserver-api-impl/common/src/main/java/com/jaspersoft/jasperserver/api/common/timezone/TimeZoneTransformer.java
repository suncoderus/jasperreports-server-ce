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
package com.jaspersoft.jasperserver.api.common.timezone;

import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.dto.adhoc.DefaultISOFormats;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import static com.jaspersoft.jasperserver.dto.adhoc.DefaultISOFormats.SIMPLE_TIMESTAMP_WITHOUT_TIMEZONE;

/**
 * A helper class which help convert time and timestamp values
 * from server to client timezone, and vice versa.
 *
 * @author Vasyl Spachynskyi
 * @version $Id: TimeZoneTransformer.java 64574 2017-11-29 16:48:19Z vspachyn $
 * @since 22.09.2017
 */
public class TimeZoneTransformer {

    public static Set<String> timeZoneIndependentPattern = new HashSet<String>(){{
        add(DefaultISOFormats.YEAR_PATTERN);
        add(DefaultISOFormats.YEAR_OF_MONTH_PATTERN);
        add(DefaultISOFormats.QUARTER_PATTERN);
        add(DefaultISOFormats.DATE_PATTERN);
    }};

    /**
     * Transform the value into {@code clientTimeZone}.
     * Timezone is not applied if the value is instance of {@code java.util.Date}
     *
     * @param value the value which will be shifted by {@code clientTimeZone}.
     *              Value have to be instance of {@code java.util.Date}.
     * @param clientTimeZone the value have to be shifted with this timezone
     * @return shifted value
     */
    public static Date toClientTimezone(Object value, TimeZone clientTimeZone) {
        if (value == null) return null;

        if (clientTimeZone == null) return (Date) value;

        if (value instanceof Timestamp) {
            return new Timestamp(toTimeZone((Date) value, TimeZone.getDefault(), clientTimeZone).getTime());
        } else if (value instanceof Time) {
            return new Time(timeToTimezoneTransformer((Date) value, TimeZone.getDefault(), clientTimeZone).getTime());
        } else if (value instanceof Date) {
            return (Date) value;
        }

        throw new IllegalArgumentException("The value type can be Date,Time and Timestamp");
    }

    public static Date toClientTimezone(Object value) {
        return toClientTimezone(value, TimeZoneContextHolder.getTimeZone());
    }

    /**
     * Apply server timezone to a time or timestamp string from client.
     * <p>
     * For example: server timezone is GMT-0700 and client is GMT-0500
     * so 14:10:00.000-0500 will be transformed to 10:10:00.000-0700.
     * Both time are the same time which are represented in different timezone.
     * <p>
     * Any non time or timestamp patterns would be ignored
     *
     * @param value a simple time or timestamp value formatted by {@link DefaultISOFormats} pattern
     * @param pattern a pattern from {@link DefaultISOFormats}* @param timeZone
     */
     public static String toTimeZone(String value, String pattern, TimeZone from, TimeZone to) {
        if (timeZoneIndependentPattern.contains(pattern))
            return value;

        try {
            Date date = parseWithTimezone(value, pattern, from);
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            format.setTimeZone(to);

            return format.format(date);
        } catch (IllegalArgumentException e) {
            // Return original value when we have expression like this MONTH-1 or DAY-1
            // we can not parse it and apply timezone so just return as is
            return value;
        }
    }

    /**
     * The generic method which help to convert tha value from one timezone to another.
     *
     *
     * @param value the value which should be transformed
     * @param from the value timezone
     * @param to the value should be transformed into {@code to} timezone
     * @return shifted value
     */
    public static Date toTimeZone(Date value, TimeZone from, TimeZone to) {
        if (value instanceof Time) {
            return timeToTimezoneTransformer(value, from, to);
        }

        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_TIMESTAMP_WITHOUT_TIMEZONE);
        format.setTimeZone(to);
        String formattedValue = format.format(value);

        return parseWithTimezone(formattedValue, SIMPLE_TIMESTAMP_WITHOUT_TIMEZONE, from);
    }

    /**
     * Apply server timezone to a value which has client timezone.
     *
     * @param value is a {@link java.sql.Timestamp} value
     * @return {@link java.sql.Timestamp} value in server timezone
     */
    public static Date toServerTimeZone(Object value) {
        if (value instanceof Time) {
            return toServerTimeZone((Time) value);
        } else if (value instanceof Timestamp){
            return toServerTimeZone((Timestamp) value);
        } else if (value instanceof Date){
            return new Date(toServerTimeZone(new Timestamp(((Date) value).getTime())).getTime());
        }

        throw new IllegalArgumentException("The value type have to ");
    }

    private static Time toServerTimeZone(Time value) {
        Date date = timeToTimezoneTransformer(value, TimeZoneContextHolder.getTimeZone(), TimeZone.getDefault());

        return new Time(date.getTime());
    }

    private static Timestamp toServerTimeZone(Timestamp value) {
        return new Timestamp(toTimeZone(value, TimeZoneContextHolder.getTimeZone(), TimeZone.getDefault()).getTime());
    }

    private static Date parseWithTimezone(String value, String pattern, TimeZone clientTimeZone) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(clientTimeZone);
        try {
            return format.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * TODO: Vasyl S. Techinal dept.
     * In Java, each DateTime values start from 1970-01-01. Even if we use java.sql.Time
     * we should count how many milliseconds spent from 1970. In that case, during formatting
     * by java SimpleDateFormat or joda DateTimeFormat Time values would depend on 1970-01-01.
     * These formatters take into account not only Daylight Saving Day(DST) they know
     * what timezone has the current country in that year. For example,
     * Europe/Kiev has two different timezones the old is +0400 which was changed after 1990 to +0300.
     * So SimpleDateFormat would convert simple java.sql.Time with +0400 timezone which
     * must not be allowed during that kind of transformations.
     * @param date original date which should be transformed
     * @param from the {@code date} parameter original timezone
     * @param to the {@code date} parameter should be transformet {@code to} timezone
     * @return
     */
    private static Date timeToTimezoneTransformer(Date date, TimeZone from, TimeZone to) {
        int fromTimezoneOffset = from.getRawOffset();
        int toTimezoneOffset = to.getRawOffset();

        long resultTime = date.getTime();

        if (fromTimezoneOffset == toTimezoneOffset) {
            return new Date(resultTime);
        }
        else if (fromTimezoneOffset >= 0 && toTimezoneOffset <= 0) {
            resultTime = date.getTime() - (Math.abs(toTimezoneOffset) + fromTimezoneOffset);
        }
        else if (fromTimezoneOffset <= 0 && toTimezoneOffset >= 0) {
            resultTime = date.getTime() + (toTimezoneOffset + Math.abs(fromTimezoneOffset));
        }
        else if (fromTimezoneOffset < 0 && toTimezoneOffset < 0) {
            if (fromTimezoneOffset > toTimezoneOffset) {
                resultTime = date.getTime() - (Math.abs(toTimezoneOffset) - Math.abs(fromTimezoneOffset));
            } else {
                resultTime = date.getTime() + (Math.abs(fromTimezoneOffset) - Math.abs(toTimezoneOffset));
            }
        }
        else if (fromTimezoneOffset > 0 && toTimezoneOffset > 0) {
            if (fromTimezoneOffset > toTimezoneOffset) {
                resultTime = date.getTime() - (fromTimezoneOffset - toTimezoneOffset);
            } else {
                resultTime = date.getTime() + (toTimezoneOffset - fromTimezoneOffset);
            }
        }

        return new Date(resultTime);
    }
}