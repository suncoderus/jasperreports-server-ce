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

package com.jaspersoft.jasperserver.dto.adhoc.query.el;

import java.util.regex.Pattern;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 * @date 3/10/16 3:07 PM
 */
public class ELUtils {

    private ELUtils() {}

    /**
     * Helper method to check if a string is composed entirely of digits. This implementation was chosen over
     * regular expression matching because it performs significantly better in time.
     *
     * @param s String to check
     * @return boolean
     */
    public static boolean isNumericString(String s) {
        if (s == null) return false;
        char[] characters = s.toCharArray();
        for (int charIndex = 0; charIndex < characters.length; charIndex++) {
            char c = characters[charIndex];
            if (!Character.isDigit(c)
                    && !((charIndex == 0)
                            && ((c == '-') || (c == '+'))
                        )) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method to determine if a string is a valid floating point number
     *
     * @param s String to check
     * @return boolean
     */
    public static boolean isFloatingPointString(String s) {
        return (s != null) && Pattern.matches("^[+-]?(\\p{Nd}+\\.?\\p{Nd}*|\\.\\p{Nd}+)(([eE][+-]?)?\\p{Nd}+)?$", s);
    }

}
