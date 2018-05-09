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

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.FLOAT;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientFloat.LITERAL_ID;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 06.02.2017
 */
@XmlRootElement(name = LITERAL_ID)
public class ClientFloat extends ClientLiteral<Float, ClientFloat> {

    public static final String LITERAL_ID = FLOAT;

    public ClientFloat() {
    }

    public ClientFloat(ClientFloat literal) {
        super(literal);
    }

    public ClientFloat(String string) {
        super(new Float(string));
    }

    public ClientFloat(Float f) {
        super(f);
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public ClientFloat setValue(Float value) {
        this.value = value;
        return this;
    }

    public static ClientFloat valueOf(String string){
        return new ClientFloat(new Float(string));
    }

    public static ClientFloat valueOf(Float value){
        return new ClientFloat(value);
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
        super.accept(visitor);
    }

    @Override
    public ClientFloat deepClone() {
        return new ClientFloat(this);
    }

}
