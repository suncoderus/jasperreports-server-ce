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

import com.jaspersoft.jasperserver.dto.adhoc.query.adapter.ELLongAdapter;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.LONG;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientLong.LITERAL_ID;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 06.02.2017
 */
@XmlRootElement(name = LITERAL_ID)
public class ClientLong extends ClientLiteral<Long, ClientLong> {

    public static final String LITERAL_ID = LONG;

    public ClientLong() {
    }

    public ClientLong(ClientLong literal) {
        super(literal);
    }

    public ClientLong(String string) {
        super(new Long(string));
    }

    public ClientLong(Long l) {
        super(l);
    }

    @Override
    @XmlJavaTypeAdapter(ELLongAdapter.class)
    public Long getValue() {
        return value;
    }

    @Override
    public ClientLong setValue(Long value) {
        this.value = value;
        return this;
    }

    public static ClientLong valueOf(String string){
        return new ClientLong(string);
    }

    public static ClientLong valueOf(Long l){
        return new ClientLong(l);
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
        super.accept(visitor);
    }

    @Override
    public ClientLong deepClone() {
        return new ClientLong(this);
    }

}
