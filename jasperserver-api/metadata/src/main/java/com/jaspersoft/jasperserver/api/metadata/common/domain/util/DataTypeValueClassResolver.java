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
package com.jaspersoft.jasperserver.api.metadata.common.domain.util;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Zakhar.Tomchenco
 *
 */
public class DataTypeValueClassResolver {

    public static Class<?> getValueClass(DataType dataType) {
        Class<?> valueClass;
        switch (dataType.getDataTypeType()) {
            case DataType.TYPE_TEXT:
                valueClass = String.class;
                break;
            case DataType.TYPE_NUMBER:
                valueClass = BigDecimal.class;
                break;
            case DataType.TYPE_DATE:
                valueClass = Date.class;
                break;
            case DataType.TYPE_DATE_TIME:
                valueClass = Timestamp.class;
                break;
            case DataType.TYPE_TIME:
                valueClass = Time.class;
                break;
            default:
                valueClass = String.class;
        }
        return valueClass;
    }
}
