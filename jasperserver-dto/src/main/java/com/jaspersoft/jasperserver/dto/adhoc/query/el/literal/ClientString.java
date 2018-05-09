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

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.STRING;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString.LITERAL_ID;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = LITERAL_ID)
public class ClientString extends ClientLiteral<String, ClientString> {

    public static final String LITERAL_ID = STRING;

    public ClientString() {
    }

    public ClientString(String string) {
        super(string);
    }

    public ClientString(ClientString s) {
        super(s);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public ClientString setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "\'" + getValue() + "\'";
    }

    public static ClientString valueOf(Character character){
        return new ClientString().setValue(character.toString());
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
        super.accept(visitor);
    }

    @Override
    public ClientString deepClone() {
        return new ClientString(this);
    }
}
