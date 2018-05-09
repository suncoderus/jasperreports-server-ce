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

import com.jaspersoft.jasperserver.dto.adhoc.query.adapter.ELBigIntegerAdapter;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigInteger;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigInteger.LITERAL_ID;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.BIG_INTEGER;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 06.02.2017
 */
@XmlRootElement(name = LITERAL_ID)
public class ClientBigInteger extends ClientLiteral<BigInteger, ClientBigInteger> {

    public static final String LITERAL_ID = BIG_INTEGER;

    public ClientBigInteger() {
    }

    public ClientBigInteger(ClientBigInteger literal) {
        super(literal);
    }

    public ClientBigInteger(String integer) {
        super(new BigInteger(integer));
    }

    public ClientBigInteger(BigInteger integer) {
        super(integer);
    }

    @Override
    @XmlJavaTypeAdapter(ELBigIntegerAdapter.class)
    public BigInteger getValue() {
        return value;
    }

    @Override
    public ClientBigInteger setValue(BigInteger value) {
        this.value = value;
        return this;
    }

    public static ClientBigInteger valueOf(String string){
        return new ClientBigInteger(new BigInteger(string));
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
        super.accept(visitor);
    }

    @Override
    public ClientBigInteger deepClone() {
        return new ClientBigInteger(this);
    }

}
