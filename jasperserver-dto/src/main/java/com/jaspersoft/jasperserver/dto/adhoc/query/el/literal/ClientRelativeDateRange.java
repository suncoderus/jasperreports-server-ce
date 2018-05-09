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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.literal;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;

import javax.xml.bind.annotation.XmlRootElement;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.adapters.DomELCommonSimpleDateFormats.DATE_RANGE_PATTERN;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = "relativeDateRange")
public class ClientRelativeDateRange extends ClientLiteral<String, ClientRelativeDateRange> {

    public ClientRelativeDateRange() {
    }

    public ClientRelativeDateRange(String relativeRange) {
        setValue(relativeRange);
    }


    public ClientRelativeDateRange(ClientRelativeDateRange d) {
        super(d);
    }


    @Override
    public String getValue() {
        return value;
    }

    public ClientRelativeDateRange setValue(String expression) {
        if (DATE_RANGE_PATTERN.matcher(expression).matches()) {
            this.value = expression;
        } else {
            throw new IllegalArgumentException("Unable to create relative date range with provided value: " +
                    expression);
        }
        return this;
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ClientRelativeDateRange deepClone() {
        return new ClientRelativeDateRange(this);
    }
}
