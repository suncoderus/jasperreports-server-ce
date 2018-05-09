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
package com.jaspersoft.jasperserver.war.util;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.HyperlinkParameterFormatter;
import com.jaspersoft.jasperserver.war.cascade.handlers.converters.DataConverter;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author inesterenko
 */
public class HyperlinkDateParameterFormatter implements HyperlinkParameterFormatter, Serializable {

    private DataConverter<Date> dateConverter;

    private DataConverter<Timestamp> timestampConverter;

    private DataConverter<Time> timeConverter;

	private static final long serialVersionUID = 1L;

    /**
     * Check whether value is of type Time, Timestamp or their descendants and apply corresponding converter,
     * if value has other type or null, apply date converter.
     * @param value Date object
     * @return String formatted date value
     */
	public String format(Object value) {
        String formattedValue;
        if (value != null) {
            if (Timestamp.class.isAssignableFrom(value.getClass())) {
                formattedValue = timestampConverter.valueToString((Timestamp) value);
            } else if (Time.class.isAssignableFrom(value.getClass())) {
                formattedValue = timeConverter.valueToString((Time) value);
            } else {
                formattedValue = dateConverter.valueToString((Date) value);
            }
        } else {
            formattedValue = dateConverter.valueToString((Date) value);
        }
        return formattedValue;
	}

    public DataConverter<Date> getDateConverter() {
        return dateConverter;
    }

    public void setDateConverter(DataConverter<Date> dateConverter) {
        this.dateConverter = dateConverter;
    }

    public DataConverter<Timestamp> getTimestampConverter() {
        return timestampConverter;
    }

    public void setTimestampConverter(DataConverter<Timestamp> timestampConverter) {
        this.timestampConverter = timestampConverter;
    }

    public DataConverter<Time> getTimeConverter() {
        return timeConverter;
    }

    public void setTimeConverter(DataConverter<Time> timeConverter) {
        this.timeConverter = timeConverter;
    }
}
