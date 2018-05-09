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
package com.jaspersoft.jasperserver.war.cascade.handlers.converters;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;

import static com.jaspersoft.jasperserver.api.common.timezone.TimeZoneTransformer.toClientTimezone;
import static com.jaspersoft.jasperserver.api.common.timezone.TimeZoneTransformer.toServerTimeZone;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class TimeDataConverter implements DataConverter<Time> {
    @Resource(name = "isoCalendarFormatProvider")
    protected CalendarFormatProvider calendarFormatProvider;

    @Override
    public Time stringToValue(String rawData) throws ParseException {
        if (!StringUtils.isNotEmpty(rawData)) return null;

        Time parsedTime = new Time(getFormatter().parse(rawData).getTime());

        return (Time) toServerTimeZone(parsedTime);
    }

    @Override
    public String valueToString(Time value) {
        return value != null ? getFormatter().format(toClientTimezone(value)) : "";
    }

    private DateFormat getFormatter() {
        return calendarFormatProvider.getTimeFormat();
    }
}
