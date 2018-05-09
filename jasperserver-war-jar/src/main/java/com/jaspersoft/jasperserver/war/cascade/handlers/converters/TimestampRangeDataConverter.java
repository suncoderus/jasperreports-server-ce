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

import net.sf.jasperreports.types.date.DateRangeExpression;
import com.jaspersoft.jasperserver.api.common.util.rd.DateRangeFactory;
import net.sf.jasperreports.types.date.TimestampRange;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
@Service
public class TimestampRangeDataConverter implements DataConverter<TimestampRange> {

    @Resource(name = "isoCalendarFormatProvider")
    protected CalendarFormatProvider calendarFormatProvider;

    @Override
    public TimestampRange stringToValue(String rawData) throws Exception {
        if (StringUtils.isEmpty(rawData)) {
            return null;
        }

        return (TimestampRange) DateRangeFactory.getInstance(rawData, Timestamp.class,
                DateRangeDataConverter.getStringDatePattern(calendarFormatProvider.getDatetimeFormat()));
    }

    @Override
    public String valueToString(TimestampRange value) {
        if (value == null) {
            return "";
        } else if (value instanceof DateRangeExpression) {
            return ((DateRangeExpression) value).getExpression();
        } else {
            return TimestampDataConverter.getDateFormatWithTimeZone(calendarFormatProvider.getDatetimeFormat())
                    .format(value.getStart());
        }
    }
}
