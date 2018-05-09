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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.adapters;

import com.jaspersoft.jasperserver.dto.adhoc.DefaultISOFormats;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * A set of SimpleDateFormat's using the format's defined in datarator-el
 *
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
public class DomELCommonSimpleDateFormats {

    private static final String DATE_RANGE_PATTERN_STRING = "^(DAY|WEEK|MONTH|QUARTER|SEMI|YEAR)([\\+|-][\\d]{1,9})?$";
    public static final Pattern DATE_RANGE_PATTERN = Pattern.compile(DATE_RANGE_PATTERN_STRING);

    public static SimpleDateFormat dateFormat() {
        return new SimpleDateFormat(DefaultISOFormats.DATE_PATTERN);
    }

    public static SimpleDateFormat timeFormat() {
        return new SimpleDateFormat(DefaultISOFormats.TIME_WITHOUT_TIMEZONE_PATTERN);
    }

    public static SimpleDateFormat timestampFormat() {
        return new SimpleDateFormat(DefaultISOFormats.SIMPLE_TIMESTAMP_WITHOUT_TIMEZONE);
    }
}
