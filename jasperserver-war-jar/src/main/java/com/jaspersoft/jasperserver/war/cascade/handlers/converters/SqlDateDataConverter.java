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

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Date;
import java.text.ParseException;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class SqlDateDataConverter implements DataConverter<Date>{
    @Resource
    private DateDataConverter dateDataConverter;
    @Override
    public Date stringToValue(String rawData) throws ParseException {
        return StringUtils.isNotEmpty(rawData) ? new Date(dateDataConverter.stringToValue(rawData).getTime()) : null;
    }

    @Override
    public String valueToString(Date value) {
        return value != null ? dateDataConverter.valueToString(value) : "";
    }
}
