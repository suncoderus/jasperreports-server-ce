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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
public enum ClientOperation {
    EQUALS("equals", "=="), NOT_EQUAL("notEqual", "!="),
    GREATER_OR_EQUAL("greaterOrEqual", ">="), LESS_OR_EQUAL("lessOrEqual", "<="), GREATER("greater", ">"), LESS("less", "<"),
    IN("in", "in"),
    NOT("not", "not"), AND("and", "and"), OR("or", "or"),
    ADD("add", "+"), SUBTRACT("subtract", "-"), DIVIDE("divide", "/"), MULTIPLY("multiply", "*"), FUNCTION("function", ""), PERCENT_FIELD_RATIO("percentRatio", "%"), UNDEFINED("undefined", "$missing$");

    private String name;
    private String domelOperator;

    ClientOperation(String name, String domelOperator) {
        this.name = name;
        this.domelOperator = domelOperator;
    }

    @XmlValue
    public String getName() {
        return name;
    }

    @XmlTransient
    public String getDomelOperator() {
        return domelOperator;
    }

    public static ClientOperation fromString(String text) {
        for (ClientOperation o : values()) {
            if (o.getName().equals(text)) {
                return o;
            }
        }

        return null;
    }

    public static ClientOperation fromDomElOperator(String operator) {
        for (ClientOperation o : values()) {
            if (o.getName().equals(operator)) {
                return o;
            }
        }

        return null;
    }

    public static boolean isSupported(String text) {
        ClientOperation co = ClientOperation.fromString(text);
        return co != null;
    }
}
