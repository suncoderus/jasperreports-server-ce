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

import com.jaspersoft.jasperserver.dto.adhoc.query.adapter.ELDecimalAdapter;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.common.ValueAcceptor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.math.BigDecimal;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigDecimal.LITERAL_ID;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.BIG_DECIMAL;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id$
 */
@XmlRootElement(name = LITERAL_ID)
public class ClientBigDecimal extends ClientLiteral<BigDecimal, ClientBigDecimal> implements ValueAcceptor<ClientBigDecimal> {

    public static final String LITERAL_ID = BIG_DECIMAL;

    public ClientBigDecimal() {
    }

    public ClientBigDecimal(String value) {
        super(new BigDecimal(value));
    }

    public ClientBigDecimal(Double value) {
        super(BigDecimal.valueOf(value));
    }

    public ClientBigDecimal(BigDecimal value) {
        super(value);
    }

    public ClientBigDecimal(ClientBigDecimal source) {
        super(source);
    }

    @Override
    @XmlJavaTypeAdapter(ELDecimalAdapter.class)
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public ClientBigDecimal setValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public static ClientBigDecimal valueOf(String string){
        return new ClientBigDecimal(string);
    }

    public static ClientBigDecimal valueOf(Double d){
        return new ClientBigDecimal(d);
    }

    public static ClientBigDecimal valueOf(BigDecimal bigDecimal){
        return new ClientBigDecimal(bigDecimal);
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ClientBigDecimal deepClone() {
        return new ClientBigDecimal(this);
    }

    @Override
    public ClientBigDecimal acceptValue(Object object) {
        if(object instanceof String){
            value = valueOf((String) object).getValue();
        } else if(object instanceof BigDecimal){
            value = valueOf((BigDecimal) object).getValue();
        } else if(object instanceof Double){
            value = valueOf((Double) object).getValue();
        } else {
            throw new IllegalArgumentException("Value of type [" + object.getClass().getName() + "] is not supported");
        }
        return this;
    }
}
