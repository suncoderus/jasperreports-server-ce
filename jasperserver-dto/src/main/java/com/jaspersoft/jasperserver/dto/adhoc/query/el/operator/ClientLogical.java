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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientOperator;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.CopyFactory;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = "logical")
public abstract class ClientLogical<F extends ClientLogical<F>> extends ClientOperator<F> {
    private ClientOperation type;
    private ClientExpression lhs;
    private ClientExpression rhs;

    public ClientLogical() {
        super();
    }

    public ClientLogical(ClientLogical logical) {
        this(logical.getType(), CopyFactory.copy(logical.getOperands()));
    }

    protected ClientLogical(ClientOperation operation, List<ClientExpression> operands) {
        super(operation.getName(), operands);
        this.type = operation;
        setOperands(operands);
    }

    public static ClientLogical createLogical(String name, List<ClientExpression> operands) {
        ClientOperation operation = ClientOperation.fromString(name);
        if (operation != null && ClientOperation.isSupported(operation.getName())) {
            if (operation.equals(ClientOperation.AND)) {
                return new ClientAnd(operands);
            } else if (operation.equals(ClientOperation.OR)) {
                return new ClientOr(operands);
            }
            return null;
        }
        return null;
    }

    public static ClientLogical createLogical(String name, List<ClientExpression> operands, boolean isParen) {
        ClientLogical result = createLogical(name, operands);
        if (isParen && result != null) {
            result.setParen();
        }
        return result;

    }

    public static ClientAnd and(ClientExpression lhs, ClientExpression rhs) {
        checkArguments(lhs, rhs);
        return new ClientAnd(asList(lhs, rhs));
    }

    public static ClientOr or(ClientExpression lhs, ClientExpression rhs) {
        checkArguments(lhs, rhs);
        return new ClientOr(asList(lhs, rhs));
    }

    static void checkArguments(ClientExpression ... expressions) throws IllegalArgumentException {
        if (expressions.length < 2) {
            throw new IllegalArgumentException("Error creating 'and/or' logical expression. Expression is not defined!");
        }
    }

    public ClientOr or(ClientExpression expression) {
        return or(this, expression);
    }

    public ClientAnd and(ClientExpression expression) {
        return and(this, expression);
    }

    @XmlTransient
    public ClientOperation getType() {
        return type;
    }

    @XmlTransient
    public ClientExpression getLhs() {
        return lhs;
    }

    @XmlTransient
    public ClientExpression getRhs() {
        return rhs;
    }

    @Override
    protected ClientOperator setOperands(List<ClientExpression> operands) {
        if (operands != null) {
            if (operands.size() > 2) {
                throw new UnsupportedOperationException("Failed to create ClientLogical with more than 2 operands");
            }
            this.operands = operands;
            if (!operands.isEmpty()) {
                this.lhs = operands.get(0);
            }
            if (operands.size() > 1) {
                this.rhs = operands.get(1);
            }
        } else {
            this.operands = null;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientLogical)) return false;
        if (!super.equals(o)) return false;

        ClientLogical<?> that = (ClientLogical<?>) o;

        if (type != that.type) return false;
        if (lhs != null ? !lhs.equals(that.lhs) : that.lhs != null) return false;
        return rhs != null ? rhs.equals(that.rhs) : that.rhs == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (lhs != null ? lhs.hashCode() : 0);
        result = 31 * result + (rhs != null ? rhs.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String lhsString = (getLhs() != null) ? getLhs().toString() : ClientExpressions.MISSING_REPRESENTATION;
        String rhsString = (getRhs() != null) ? getRhs().toString() : ClientExpressions.MISSING_REPRESENTATION;

        if (hasParen()) {
            sb.append("(");
        }

        sb
                .append(lhsString)
                .append(" ")
                .append(getType().getName())
                .append(" ")
                .append(rhsString);

        if (hasParen()) {
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        if (this.getLhs() != null) {
            this.getLhs().accept(visitor);
        }
        if (this.getRhs() != null) {
            this.getRhs().accept(visitor);
        }
    }
}
