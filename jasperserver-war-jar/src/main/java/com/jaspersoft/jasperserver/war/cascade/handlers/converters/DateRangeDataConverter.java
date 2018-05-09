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
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import net.sf.jasperreports.types.date.DateRange;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Anton Fomin
 * @version $Id$
 */
@Service
public class DateRangeDataConverter implements DataConverter<DateRange> {

    @Resource(name = "isoCalendarFormatProvider")
    protected CalendarFormatProvider calendarFormatProvider;

    @Override
    public DateRange stringToValue(String rawData) throws Exception {
        if (StringUtils.isEmpty(rawData)) {
            return null;
        }

        return DateRangeFactory.getInstance(rawData, Date.class,
                getStringDatePattern(calendarFormatProvider.getDateFormat()));
    }

    @Override
    public String valueToString(DateRange value) {
        if (value == null) {
            return "";
        } else if (value instanceof DateRangeExpression) {
            return ((DateRangeExpression) value).getExpression();
        } else {
            return calendarFormatProvider.getDateFormat().format(value.getStart());
        }
    }

    public static String getStringDatePattern(DateFormat dateFormat) {
        return ((SimpleDateFormat) dateFormat).toPattern();
    }

}
