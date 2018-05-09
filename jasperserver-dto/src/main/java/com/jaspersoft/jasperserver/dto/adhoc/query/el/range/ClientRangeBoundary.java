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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.range;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientFloat;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDate;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDouble;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientLong;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientShort;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientByte;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigDecimal;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTime;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
@XmlRootElement
public class ClientRangeBoundary implements ClientExpression<ClientRangeBoundary> {

    private ClientExpression boundary;
    private Boolean paren;

    public ClientRangeBoundary() {
    }

    public ClientRangeBoundary(ClientLiteral literal) {
        this.boundary = literal.deepClone();
    }

    public ClientRangeBoundary(ClientExpression literal) {
        this.boundary = (ClientExpression) literal.deepClone();
    }

    public ClientRangeBoundary(ClientRangeBoundary boundary) {
        final ClientExpression<? extends ClientExpression> sourceBound = boundary.getBoundary();
        if(sourceBound != null){
            this.boundary = sourceBound.deepClone();
        }
    }

    @XmlElements({
            @XmlElement(name = ClientByte.LITERAL_ID,
                    type = ClientByte.class),
            @XmlElement(name = ClientShort.LITERAL_ID,
                    type = ClientShort.class),
            @XmlElement(name = ClientInteger.LITERAL_ID,
                    type = ClientInteger.class),
            @XmlElement(name = ClientLong.LITERAL_ID,
                    type = ClientLong.class),
            @XmlElement(name = ClientBigInteger.LITERAL_ID,
                    type = ClientBigInteger.class),
            @XmlElement(name = ClientFloat.LITERAL_ID,
                    type = ClientFloat.class),
            @XmlElement(name = ClientDouble.LITERAL_ID,
                    type = ClientDouble.class),
            @XmlElement(name = ClientBigDecimal.LITERAL_ID,
                    type = ClientBigDecimal.class),
            @XmlElement(name = ClientString.LITERAL_ID,
                    type = ClientString.class),
            @XmlElement(name = ClientBoolean.LITERAL_ID,
                    type = ClientBoolean.class),
            @XmlElement(name = ClientDate.LITERAL_ID,
                    type = ClientDate.class),
            @XmlElement(name = ClientTimestamp.LITERAL_ID,
                    type = ClientTimestamp.class),
            @XmlElement(name = ClientTime.LITERAL_ID,
                    type = ClientTime.class),
            @XmlElement(name = ClientVariable.EXPRESSION_TYPE_VARIABLE,
                    type = ClientVariable.class),
            @XmlElement(name = ClientFunction.FUNCTION_ID,
                    type = ClientFunction.class)
    })
    public ClientExpression getBoundary() {
        return boundary;
    }
    public ClientRangeBoundary setBoundary(ClientExpression bound) {
        this.boundary = bound;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientRangeBoundary)) return false;

        ClientRangeBoundary that = (ClientRangeBoundary) o;

        if (boundary != null ? !boundary.equals(that.boundary) : that.boundary != null) return false;
        return paren != null ? paren.equals(that.paren) : that.paren == null;

    }

    @Override
    public int hashCode() {
        int result = boundary != null ? boundary.hashCode() : 0;
        result = 31 * result + (paren != null ? paren.hashCode() : 0);
        return result;
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        if (boundary != null) {
            boundary.accept(visitor);
        }
    }

    public Boolean isParen() {
        return (paren == null) ? null : paren;
    }

    @Override
    public Boolean hasParen() {
        return isParen() != null && paren;
    }

    @Override
    public ClientRangeBoundary deepClone() {
        return new ClientRangeBoundary(this);
    }
}
