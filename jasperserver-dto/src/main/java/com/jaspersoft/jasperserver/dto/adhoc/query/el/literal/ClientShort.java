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

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.SHORT;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientShort.LITERAL_ID;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 06.02.2017
 */
@XmlRootElement(name = LITERAL_ID)
public class ClientShort extends ClientLiteral<Short, ClientShort> {

    public static final String LITERAL_ID = SHORT;

    public ClientShort() {
    }

    public ClientShort(ClientShort literal) {
        super(literal);
    }

    public ClientShort(Short s) {
        super(s);
    }

    public ClientShort(String s) {
        super(new Short(s));
    }

    public static ClientShort valueOf(String string) {
        return new ClientShort(string);
    }

    @Override
    public Short getValue() {
        return value;
    }

    @Override
    public ClientShort setValue(Short value) {
        this.value = value;
        return this;
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
        super.accept(visitor);
    }

    @Override
    public ClientShort deepClone() {
        return new ClientShort(this);
    }

}
