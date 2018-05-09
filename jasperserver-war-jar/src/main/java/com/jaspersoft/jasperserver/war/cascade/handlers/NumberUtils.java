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

package com.jaspersoft.jasperserver.war.cascade.handlers;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Yaroslav.Kovalchyk
 * @author Sergey Prilukin
 * @version $Id$
 */
public class NumberUtils {

    /**
     * We should convert each number to {@link java.math.BigDecimal} to compare different number types: Byte, Short, Integer, Long, Float, Double, BigDecimal, etc
     * @param number Number to convert
     * @return BigDecimal
     */
    public static BigDecimal toBigDecimal(Number number) {
        if (number.getClass() == Integer.class || number.getClass() == Long.class ||
                number.getClass() ==  Short.class || number.getClass() == Byte.class) {

            return BigDecimal.valueOf(number.longValue());

        } else if (number.getClass() == Double.class || number.getClass() == Float.class) {

            return BigDecimal.valueOf(number.doubleValue());

        } else if (number.getClass() == BigDecimal.class) {

            return (BigDecimal) number;

        } else if (number.getClass() == BigInteger.class) {

            return new BigDecimal((BigInteger) number);

        }

        try {
            return new BigDecimal(number.toString());
        } catch(final NumberFormatException e) {
            throw new RuntimeException("The given number (\"" + number
                    + "\" of class " + number.getClass().getName()
                    + ") does not have a parseable string representation", e);
        }
    }
}
