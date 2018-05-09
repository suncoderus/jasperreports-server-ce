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

package com.jaspersoft.jasperserver.core.util;

/**
 * @author Fedir Sajbert
 */
public class XSSEscapeUtil {

    /**
     * <p>Cleans particular string through removing of symbols which might cause possible vulnerabilities.</p>
     *
     * @param value passed string for XSS check.
     *
     * @return clean string after XSS check.
     */
    public static String cleanXSS(String value) {
        if(value != null){
            value = value.replaceAll("(?i)eval\\((.*)\\)", "");
            value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
            value = value.replaceAll("(?i)<script.*?>.*?</script.*?>", "");
            value = value.replaceAll("(?<![a-zA-z])script(?![a-zA-z])|javascript", "");
        }
        return value;
    }

}

