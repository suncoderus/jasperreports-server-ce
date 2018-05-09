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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientAdd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientPercentRatio;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientSubtract;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientDivide;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientMultiply;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.*;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
public abstract class ClientOperator<F extends ClientOperator<F>> implements ClientExpression<F> {

    protected String operator;

    @NotNull
    @Valid
    protected List<ClientExpression> operands;
    protected Boolean paren = null;

    public ClientOperator() {
        this.operands = new ArrayList<ClientExpression>();
        this.operator = ClientOperation.UNDEFINED.getName();
    }

    public ClientOperator(String operator) {
        this();
        this.operator = operator;
    }

    public ClientOperator(String operator, List<? extends ClientExpression> operands) {
        this(operator);
        this.operands = new ArrayList<ClientExpression>(operands);
    }

    /*
        This is specifically for use by copy constructors!
     */
    protected ClientOperator(String operator, List<? extends ClientExpression> operands, Boolean paren) {
        this(operator);
        this.operands = CopyFactory.copy(operands);
        this.paren = paren;
    }

    ClientOperator(ClientExpression expr1, ClientExpression expr2) {
        this();
        this.operands.add(CopyFactory.copy(expr1));
        this.operands.add(CopyFactory.copy(expr2));
    }

    @XmlTransient
    public String getOperator() {
        return operator;
    }

    protected ClientOperator setOperator(String operator) {
        this.operator = operator;
        return this;
    }

    @XmlElementWrapper(name = "operands")
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
            @XmlElement(name = "long",
                    type = ClientLong.class),
            @XmlElement(name = "bigInteger",
                    type = ClientBigInteger.class),
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
    @Size(min = 1, message = "query.expression.is.not.valid")
    public List<ClientExpression> getOperands() {
        return operands;
    }

    protected ClientOperator setOperands(List<ClientExpression> operands) {
        this.operands = new ArrayList<ClientExpression>();
        for (ClientExpression operand : operands) {
            this.operands.add(operand);
        }
        return this;
    }

    public F addOperand(ClientExpression operand){
        if(this.operands == null){
            this.operands = new ArrayList<ClientExpression>();
        }
        this.operands.add(operand);
        return (F) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientOperator)) return false;

        ClientOperator<?> that = (ClientOperator<?>) o;

        if (operator != null ? !operator.equals(that.operator) : that.operator != null) return false;
        if (operands != null ? !operands.equals(that.operands) : that.operands != null) return false;
        return paren != null ? paren.equals(that.paren) : that.paren == null;

    }

    @Override
    public int hashCode() {
        int result = operator != null ? operator.hashCode() : 0;
        result = 31 * result + (operands != null ? operands.hashCode() : 0);
        result = 31 * result + (paren != null ? paren.hashCode() : 0);
        return result;
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);

        if (getOperands() != null) {
            for (ClientExpression expr : getOperands()) {
                if (expr != null) {
                    expr.accept(visitor);
                }
            }
        }
    }

    @Override
    public Boolean hasParen() {
        return paren != null && paren;
    }

    @XmlElement(name = "paren")
    public Boolean isParen() {
        return (paren == null) ? null : paren;
    }

    public ClientOperator setParen() {
        paren = Boolean.TRUE;
        return this;
    }

    /**
     * We set paren to null, to avoid serialization.
     *
     * @return
     */
    public ClientOperator unsetParen() {
        paren = null;
        return this;
    }

    protected String addStringOperand(ClientExpression operand) {
        StringBuilder result = new StringBuilder();
        //if there are no parens for inner function we have to add them to maintain order
        if (operand instanceof ClientOperator && !operand.hasParen()) {
            result.append("(").append(operand.toString()).append(")");
        } else {
            result.append(operand.toString());
        }
        return result.toString();
    }

}
