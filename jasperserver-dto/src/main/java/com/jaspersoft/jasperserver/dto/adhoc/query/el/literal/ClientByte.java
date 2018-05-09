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

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientByte.LITERAL_ID;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.BYTE;


/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 06.02.2017
 */
@XmlRootElement(name = LITERAL_ID)
public class ClientByte extends ClientLiteral<Byte, ClientByte> {

    public static final String LITERAL_ID = BYTE;

    public ClientByte() {
    }

    public ClientByte(ClientByte literal) {
        super(literal);
    }

    public ClientByte(Byte b) {
        super(b);
    }

    public ClientByte(String string) {
        super(new Byte(string));
    }

    @Override
    public Byte getValue() {
        return value;
    }

    @Override
    public ClientByte setValue(Byte value) {
        this.value = value;
        return this;
    }

    public static ClientByte valueOf(String string){
        return new ClientByte(new Byte(string));
    }

    public static ClientByte valueOf(Byte b){
        return new ClientByte(b);
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
        super.accept(visitor);
    }

    @Override
    public ClientByte deepClone() {
        return new ClientByte(this);
    }

}
