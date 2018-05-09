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

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 */
public class DefaultISOFormats {
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String TIME_PATTERN = "HH:mm:ss.SSSZ";
    public static final String TIME_WITHOUT_TIMEZONE_PATTERN = "HH:mm:ss.SSS";
    public static final String TIME_WITHOUT_MILLISECONDS_PATTERN = "HH:mm:ss";

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String SIMPLE_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSSZ";
    public static final String SIMPLE_TIMESTAMP_WITHOUT_TIMEZONE = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String TIMESTAMP_WITHOUT_MILLISECONDS_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String YEAR_OF_MONTH_PATTERN = "yyyy-MM";
    public static final String QUARTER_PATTERN = "yyyy-Q";
    public static final String YEAR_PATTERN = "yyyy";
    public static final String DAY_OF_WEEK_PATTERN = "EEEE";

}