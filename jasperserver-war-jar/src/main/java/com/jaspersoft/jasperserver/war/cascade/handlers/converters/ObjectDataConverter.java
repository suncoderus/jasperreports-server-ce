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

import com.jaspersoft.jasperserver.war.cascade.handlers.InputControlHandler;
import org.springframework.stereotype.Service;

/**
 * @author inesterenko
 * @version $Id$
 */

@Service
public class ObjectDataConverter implements DataConverter<Object> {
    @Override
    public Object stringToValue(String rawData) {
        return rawData;
    }

    @Override
    public String valueToString(Object value) {
        return value == null ? InputControlHandler.NULL_SUBSTITUTION_VALUE : value.toString();
    }
}
