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
package com.jaspersoft.jasperserver.dto.adhoc.query.el;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeTimestampRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeDateRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientPercentRatio;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientAdd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientSubtract;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientDivide;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientMultiply;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreater;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreaterOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLess;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLessOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientNotEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ClientList implements ClientExpression<ClientList> {

    public List<ClientExpression> items;
    protected Boolean paren = null;

    public ClientList() {
        this.items = new ArrayList<ClientExpression>();
    }

    public ClientList(List<ClientExpression> expressions) {
        this();
        this.items = CopyFactory.copy(expressions);
    }

    public ClientList (ClientList source){
        this(source.getItems());
    }

    public ClientList addItem(ClientExpression expr) {
        this.items.add(expr);
        return this;
    }

    public ClientList deleteItem(ClientExpression expr) {
        this.items.remove(expr);
        return this;
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        if (items != null) {
            for (ClientExpression expr : items) {
                expr.accept(visitor);
            }
        }
        visitor.visit(this);
    }

    @XmlElementWrapper(name = "items")
    @XmlElements(value = {
            @XmlElement(name = "NULL",
                    type = ClientNull.class),
            @XmlElement(name = "boolean",
                    type = ClientBoolean.class),
            @XmlElement(name = "byte",
                    type = ClientByte.class),
            @XmlElement(name = "short",
                    type = ClientShort.class),
            @XmlElement(name = "integer",
                    type = ClientInteger.class),
            @XmlElement(name = "bigInteger",
                    type = ClientBigInteger.class),
            @XmlElement(name = "long",
                    type = ClientLong.class),
            @XmlElement(name = "relativeDateRange",
                    type = ClientRelativeDateRange.class),
            @XmlElement(name = "relativeTimestampRange",
                    type = ClientRelativeTimestampRange.class),
            @XmlElement(name = "string",
                    type = ClientString.class),
            @XmlElement(name = "date",
                    type = ClientDate.class),
            @XmlElement(name = "time",
                    type = ClientTime.class),
            @XmlElement(name = "timestamp",
                    type = ClientTimestamp.class),
            @XmlElement(name = "float",
                    type = ClientFloat.class),
            @XmlElement(name = "double",
                    type = ClientDouble.class),
            @XmlElement(name = "bigDecimal",
                    type = ClientBigDecimal.class),
            @XmlElement(name = "variable",
                    type = ClientVariable.class),
            @XmlElement(name = "not",
                    type = ClientNot.class),
            @XmlElement(name = "and",
                    type = ClientAnd.class),
            @XmlElement(name = "or",
                    type = ClientOr.class),
            @XmlElement(name = "greater",
                    type = ClientGreater.class),
            @XmlElement(name = "greaterOrEqual",
                    type = ClientGreaterOrEqual.class),
            @XmlElement(name = "less",
                    type = ClientLess.class),
            @XmlElement(name = "lessOrEqual",
                    type = ClientLessOrEqual.class),
            @XmlElement(name = "notEqual",
                    type = ClientNotEqual.class),
            @XmlElement(name = "equals",
                    type = ClientEquals.class),
            @XmlElement(name = "function",
                    type = ClientFunction.class),
            @XmlElement(name = "in",
                    type = ClientIn.class),
            @XmlElement(name = "range",
                    type = ClientRange.class),
            @XmlElement(name = "add",
                    type = ClientAdd.class),
            @XmlElement(name = "subtract",
                    type = ClientSubtract.class),
            @XmlElement(name = "multiply",
                    type = ClientMultiply.class),
            @XmlElement(name = "divide",
                    type = ClientDivide.class),
            @XmlElement(name = "percentRatio",
                    type = ClientPercentRatio.class),
            @XmlElement(name = "list",
                    type = ClientList.class)
    })
    public List<ClientExpression> getItems() {
        return items;
    }

    public ClientList setItems(List<ClientExpression> items) {
        this.items = items;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientList)) return false;

        ClientList that = (ClientList) o;

        if (items != null ? !items.equals(that.items) : that.items != null) return false;
        return paren != null ? paren.equals(that.paren) : that.paren == null;

    }

    @Override
    public int hashCode() {
        int result = items != null ? items.hashCode() : 0;
        result = 31 * result + (paren != null ? paren.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        if (this.items == null || this.items.isEmpty()) {
            return "()";
        }

        StringBuilder sb = new StringBuilder("(");
        for (ClientExpression expression : this.items) {
            sb.append(expression.toString());
            if (this.items.indexOf(expression) != this.items.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Boolean hasParen() {
        return isParen() != null && paren;
    }

    public Boolean isParen() {
        return (paren != null) ? paren : null;
    }

    @Override
    public ClientList deepClone() {
        return new ClientList(this);
    }
}
